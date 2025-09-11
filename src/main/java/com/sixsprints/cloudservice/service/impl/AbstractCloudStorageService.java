package com.sixsprints.cloudservice.service.impl;

import static java.util.UUID.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import javax.imageio.ImageIO;
import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.CloudStorage;

public abstract class AbstractCloudStorageService implements CloudStorage {

  @Override
  public String resizeAndUpload(final FileDto fileDto, final String bucket,
      final Double maxImageSize) {
    BufferedImage bufferedImage = fileToBufferedImage(fileDto);
    bufferedImage = resizeImage(bufferedImage, maxImageSize);
    File resizedFile = fileDtoToFile(fileDto);
    writeBufferedToFile(bufferedImage, resizedFile);
    return upload(cloneFileDto(fileDto, resizedFile), bucket);
  }

  @Override
  public Path download(String key, String bucket) throws IOException {
    Path tmp = Files.createTempDirectory(null, new FileAttribute<?>[0]);
    return download(key, bucket, tmp.toAbsolutePath().toString());
  }

  protected FileDto cloneFileDto(FileDto fileDto, File resizedFile) {
    return FileDto.builder().fileName(fileDto.getFileName()).fileToUpload(resizedFile).build();
  }

  protected void writeBufferedToFile(BufferedImage bufferedImage, File resizedFile) {
    try {
      ImageIO.write(bufferedImage, "jpg", resizedFile);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to re-write buffered image back to file");
    }
  }

  protected BufferedImage fileToBufferedImage(FileDto fileDto) {
    try {
      if (fileDto.getBytes() != null && fileDto.getBytes().length > 0) {
        return ImageIO.read(new ByteArrayInputStream(fileDto.getBytes()));
      }
      return ImageIO.read(fileDto.getFileToUpload());
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to convert file to buffered image");
    }

  }

  protected byte[] fileToBytes(FileDto dto) {
    if (dto.getBytes() != null && dto.getBytes().length > 0) {
      return dto.getBytes();
    }
    final File file = dto.getFileToUpload();
    try {
      return Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid file passed to upload");
    }
  }

  protected File fileDtoToFile(FileDto fileDto) {
    if (fileDto.getFileToUpload() != null) {
      return fileDto.getFileToUpload();
    }
    try {
      return Files.write(
          createTempFile(randomUUID().toString() + fileDto.getFileName(),
              Files.createTempDirectory(null, new FileAttribute<?>[0]).toAbsolutePath().toString()),
          fileDto.getBytes()).toFile();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

  protected static BufferedImage resizeImage(BufferedImage originalImage, Double maxImageSize) {

    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

    Double width = Double.valueOf(originalImage.getWidth());
    Double height = Double.valueOf(originalImage.getHeight());
    if (width < maxImageSize && height < maxImageSize) {
      return originalImage;
    }
    if (width > height) {
      height = maxImageSize * (height / width);
      width = maxImageSize;
    } else {
      width = maxImageSize * (width / height);
      height = maxImageSize;
    }

    BufferedImage resizedImage = new BufferedImage(width.intValue(), height.intValue(), type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, width.intValue(), height.intValue(), null);
    g.dispose();
    return resizedImage;
  }

  protected Path createTempFile(String key, String dir) throws IOException {
    Path path = Paths.get(dir, randomUUID().toString());
    Files.createDirectories(path);
    return Paths.get(path.toAbsolutePath().toString(), key.replaceAll("/", "-"));
  }

}
