package com.sixsprints.storage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.amazonaws.regions.Regions;
import com.google.common.collect.Lists;
import com.sixsprints.storage.dto.Credentials;
import com.sixsprints.storage.dto.FileDto;
import com.sixsprints.storage.service.CloudStorage;
import com.sixsprints.storage.service.impl.S3CloudStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3StorageTest {

  private static final String ACCESS_ID = "AKIAVICIPFVWGA6LD5BO";

  private static final String SECRET_KEY = "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";

  private static final String BUCKET_NAME = "cloudscans";

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
  public void shouldProcessBatch() throws IOException {
    CloudStorage storageService = storage();
    storageService.downloadAndBatchProcess("out.csv", BUCKET_NAME, 100, this::process);
  }

  private CloudStorage storage() {
    CloudStorage storageService = new S3CloudStorage(
      Credentials.builder().accessId(ACCESS_ID).secretKey(SECRET_KEY).region(Regions.AP_SOUTH_1)
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
