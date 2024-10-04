package com.sixsprints.cloudservice.service.impl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;
import com.sixsprints.cloudservice.dto.Credentials;
import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.CloudStorage;

public class GoogleCloudStorage extends AbstractCloudStorageService implements CloudStorage {

  private static final String BASE_URL = "https://storage.googleapis.com/";

  final Storage storage;

  public GoogleCloudStorage(Credentials cred) {
    try {
      com.google.auth.Credentials credentials = GoogleCredentials.fromStream(cred.getFile());
      this.storage = StorageOptions.newBuilder()
        .setCredentials(credentials)
        .setProjectId(cred.getProjectId())
        .build()
        .getService();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Credentials Passed");
    }
  }

  @Override
  public String upload(final FileDto fileDto, final String bucket) {
    final String fileName = fileDto.getFileName();
    byte[] bytes = fileToBytes(fileDto);
    storage.create(
      BlobInfo.newBuilder(bucket, fileName).build(), bytes);
    return new StringBuffer(BASE_URL)
      .append(bucket)
      .append("/")
      .append(fileDto.getFileName())
      .toString();
  }

  @Override
  public Path download(String key, String bucket, String dir) throws IOException {
    Path outputFile = createTempFile(key, dir);
    Blob blob = storage.get(BlobId.of(bucket, key));
    blob.downloadTo(outputFile);
    return outputFile;
  }
  
  @Override
  public boolean doesObjectExist(String key, String bucket, String dir) {
    try {
    	Page<Blob> blobs =  storage.list(bucket, BlobListOption.currentDirectory(), BlobListOption.prefix(dir + key));
    	Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
    	while (blobIterator.hasNext()) {
    		return true;
    	}
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public URL getPresignedURL(Integer validityInDays, String key, String bucket, String dir) {
    try {
      if (validityInDays == null) {
    	  return storage.signUrl(BlobInfo.newBuilder(bucket, dir + key).build(), 30, TimeUnit.MINUTES, Storage.SignUrlOption.withVirtualHostedStyle());
      } else {
    	  return storage.signUrl(BlobInfo.newBuilder(bucket, dir + key).build(), validityInDays, TimeUnit.DAYS, Storage.SignUrlOption.withVirtualHostedStyle());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}