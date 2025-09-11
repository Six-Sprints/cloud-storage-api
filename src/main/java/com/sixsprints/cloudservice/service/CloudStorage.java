package com.sixsprints.cloudservice.service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import com.sixsprints.cloudservice.dto.FileDto;

/**
 * Interface for cloud storage operations including file upload, download, and management.
 * Provides methods for interacting with cloud storage services like AWS S3, Google Cloud Storage, etc.
 */
public interface CloudStorage {

  /**
   * Uploads a file to the specified cloud storage bucket.
   *
   * @param fileDto the file data transfer object containing file information and content
   * @param bucket the name of the cloud storage bucket where the file will be uploaded
   * @return the url of the uploaded file in the cloud storage
   */
  String upload(FileDto fileDto, String bucket);

  /**
   * Resizes an image file and uploads it to the specified cloud storage bucket.
   * The image will be resized to fit within the specified maximum dimensions while maintaining aspect ratio.
   *
   * @param fileDto the file data transfer object containing image file information and content
   * @param bucket the name of the cloud storage bucket where the resized image will be uploaded
   * @param maxImageSize the maximum size (in pixels) for the resized image dimensions
   * @return the url of the uploaded resized image in the cloud storage
   */
  String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);

  /**
   * Downloads a file from the specified cloud storage bucket to a temporary location.
   *
   * @param key the unique identifier/key of the file in the cloud storage
   * @param bucket the name of the cloud storage bucket containing the file
   * @return the local path where the downloaded file is stored
   * @throws IOException if an I/O error occurs during the download process
   */
  Path download(String key, String bucket) throws IOException;

  /**
   * Downloads a file from the specified cloud storage bucket to a specific directory.
   *
   * @param key the unique identifier/key of the file in the cloud storage
   * @param bucket the name of the cloud storage bucket containing the file
   * @param dir the directory path within the bucket where the file is located
   * @return the local path where the downloaded file is stored
   * @throws IOException if an I/O error occurs during the download process
   */
  Path download(String key, String bucket, String dir) throws IOException;

  /**
   * Checks if an object exists in the specified cloud storage bucket and directory.
   *
   * @param key the unique identifier/key of the object to check
   * @param bucket the name of the cloud storage bucket to check
   * @return true if the object exists, false otherwise
   */
  boolean doesObjectExist(String key, String bucket);

  /**
   * Generates a presigned URL for accessing a file in cloud storage without authentication.
   * The URL will be valid for the specified duration.
   *
   * @param validity the time unit for the URL validity period (e.g., TimeUnit.HOURS, TimeUnit.DAYS)
   * @param validityValue the numeric value for the validity period in the specified time unit
   * @param key the unique identifier/key of the file in the cloud storage
   * @param bucket the name of the cloud storage bucket containing the file
   * @return a presigned URL that allows temporary access to the file
   * @throws IllegalArgumentException if any parameter is null, empty, or invalid
   * @throws IOException if an error occurs while generating the presigned URL
   */
  URL getPresignedURL(TimeUnit validity, Integer validityValue, String key, String bucket);

}
