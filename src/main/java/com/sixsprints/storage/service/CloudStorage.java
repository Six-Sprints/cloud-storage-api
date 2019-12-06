package com.sixsprints.storage.service;

import com.sixsprints.storage.dto.FileDto;

public interface CloudStorage {

  String upload(FileDto fileDto, String bucket);
  
  String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);

}
