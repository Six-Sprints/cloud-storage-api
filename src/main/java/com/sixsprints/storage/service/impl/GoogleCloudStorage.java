package com.sixsprints.storage.service.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.sixsprints.storage.dto.Credentials;
import com.sixsprints.storage.dto.FileDto;
import com.sixsprints.storage.service.CloudStorage;

public class GoogleCloudStorage implements CloudStorage {

  private static final String BASE_URL = "https://storage.googleapis.com/";

  final Storage storage;

  public GoogleCloudStorage(Credentials cred) {
    try {
      com.google.auth.Credentials credentials = GoogleCredentials
        .fromStream(new FileInputStream(cred.getFile()));
      this.storage = StorageOptions.newBuilder().setCredentials(credentials)
        .setProjectId(cred.getProjectId()).build().getService();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Credentials Passed");
    }
  }

  @Override
  public String upload(final FileDto fileDto, final String bucket) {
    final String fileName = fileDto.getFileName();
    byte[] bytes = fileToBytes(fileDto.getFileToUpload());
    storage.create(
      BlobInfo.newBuilder(bucket, fileName).build(), bytes);
    return new StringBuffer(BASE_URL).append(bucket).append("/").append(fileDto.getFileName()).toString();
  }

  @Override
  public String resizeAndUpload(final FileDto fileDto, final String bucket, final Double maxImageSize) {
    BufferedImage bufferedImage = fileToBufferedImage(fileDto);
    bufferedImage = resizeImage(bufferedImage, maxImageSize);
    File resizedFile = fileDto.getFileToUpload();
    writeBufferedToFile(bufferedImage, resizedFile);
    return upload(cloneFileDto(fileDto, resizedFile), bucket);
  }

  private FileDto cloneFileDto(FileDto fileDto, File resizedFile) {
    return FileDto.builder().fileName(fileDto.getFileName()).fileToUpload(resizedFile).build();
  }

  private void writeBufferedToFile(BufferedImage bufferedImage, File resizedFile) {
    try {
      ImageIO.write(bufferedImage, "jpg", resizedFile);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to re-write buffered image back to file");
    }
  }

  private BufferedImage fileToBufferedImage(FileDto fileDto) {
    try {
      return ImageIO.read(fileDto.getFileToUpload());
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to convert file to buffered image");
    }

  }

  private byte[] fileToBytes(final File file) {
    try {
      return Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid file passed to upload");
    }
  }

  private static BufferedImage resizeImage(BufferedImage originalImage, Double maxImageSize) {

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

}
