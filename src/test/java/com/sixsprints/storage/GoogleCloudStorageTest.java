package com.sixsprints.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.sixsprints.storage.dto.Credentials;
import com.sixsprints.storage.dto.FileDto;
import com.sixsprints.storage.service.CloudStorage;
import com.sixsprints.storage.service.impl.GoogleCloudStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleCloudStorageTest {

  private static final String PROJECT_ID = "six-sprints-cloud";

  private static final String BUCKET_NAME = "smart-departure-dev";

  private static final String AUTH_JSON = "storage-cred-dev.json";

  @Test
  public void shouldUpload() throws IOException {
    CloudStorage storageService = storage();
    String upload = storageService.upload(createFileDto(0), BUCKET_NAME);
    System.out.println(upload);
  }

  @Test
  public void shouldResizeAndUpload() throws IOException {
    CloudStorage storageService = storage();
    String upload = storageService.resizeAndUpload(createFileDto(1), BUCKET_NAME, 50D);
    System.out.println(upload);
  }

  @Test
  public void shouldDownload() throws IOException {
    CloudStorage storageService = storage();
    Path path = storageService.download("0flower.jpeg", BUCKET_NAME);
    System.out.println(path);
    Files.delete(path);
  }

  @Test
  public void shouldProcessBatch() throws IOException {
    CloudStorage storageService = storage();
    storageService.downloadAndBatchProcess("out.csv", BUCKET_NAME, 100, this::process);
  }

  private CloudStorage storage() throws IOException {
    InputStream stream = Resources.getResource(AUTH_JSON).openStream();
    CloudStorage storageService = new GoogleCloudStorage(
      Credentials.builder().file(stream).projectId(PROJECT_ID)
        .build());
    return storageService;
  }

  private FileDto createFileDto(int i) {
    return FileDto.builder().fileName(i + "flower.jpeg")
      .fileToUpload(new File("/Users/karan/Desktop/Misc/Pics/download.jpeg"))
      .build();
  }

  private List<String> process(List<String> batch) {
    log.info("Batch of size {} recieved for processing", batch.size());
    return Lists.newArrayList();
  }

}
