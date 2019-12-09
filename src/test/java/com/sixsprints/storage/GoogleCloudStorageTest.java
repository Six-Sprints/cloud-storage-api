package com.sixsprints.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.sixsprints.storage.dto.Credentials;
import com.sixsprints.storage.dto.FileDto;
import com.sixsprints.storage.service.CloudStorage;
import com.sixsprints.storage.service.impl.GoogleCloudStorage;

public class GoogleCloudStorageTest {

  private static final String PROJECT_ID = "six-sprints-cloud";

  private static final String BUCKET_NAME = "smart-departure-dev";

  private static final String AUTH_JSON = "/Users/karan/Desktop/temp/google.json";

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

  private CloudStorage storage() throws FileNotFoundException {
    CloudStorage storageService = new GoogleCloudStorage(
      Credentials.builder().file(new FileInputStream(new File(AUTH_JSON))).projectId(PROJECT_ID)
        .build());
    return storageService;
  }

  private FileDto createFileDto(int i) {
    return FileDto.builder().fileName(i + "flower.jpeg")
      .fileToUpload(new File("/Users/karan/Desktop/Misc/Pics/download.jpeg"))
      .build();
  }

}
