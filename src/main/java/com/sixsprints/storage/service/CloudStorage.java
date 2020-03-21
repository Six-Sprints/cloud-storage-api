package com.sixsprints.storage.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import com.sixsprints.storage.dto.FileDto;

public interface CloudStorage {

  String upload(FileDto fileDto, String bucket);

  String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);

  Path download(String key, String bucket) throws IOException;

  Path download(String key, String bucket, String dir) throws IOException;

  <T> List<T> downloadAndBatchProcess(String key, String bucket, int batchSize, Function<List<String>, List<T>> func)
    throws IOException;

}
