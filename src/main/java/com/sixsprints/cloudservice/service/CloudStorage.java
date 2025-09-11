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
   * @return the unique key/identifier for the uploaded file in the cloud storage
   * @throws IllegalArgumentException if fileDto or bucket is null or invalid
   * @throws IOException if an I/O error occurs during the upload process
   */
  String upload(FileDto fileDto, String bucket);

  /**
   * Resizes an image file and uploads it to the specified cloud storage bucket.
   * The image will be resized to fit within the specified maximum dimensions while maintaining aspect ratio.
   *
   * @param fileDto the file data transfer object containing image file information and content
   * @param bucket the name of the cloud storage bucket where the resized image will be uploaded
   * @param maxImageSize the maximum size (in pixels) for the resized image dimensions
   * @return the unique key/identifier for the uploaded resized image in the cloud storage
   * @throws IllegalArgumentException if fileDto, bucket is null, or maxImageSize is invalid
   * @throws IOException if an I/O error occurs during the resize or upload process
   * @throws UnsupportedOperationException if the file is not a supported image format
   */
  String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);

  /**
   * Downloads a file from the specified cloud storage bucket to a temporary location.
   *
   * @param key the unique identifier/key of the file in the cloud storage
   * @param bucket the name of the cloud storage bucket containing the file
   * @return the local path where the downloaded file is stored
   * @throws IOException if an I/O error occurs during the download process
   * @throws IllegalArgumentException if key or bucket is null or empty
   * @throws java.util.NoSuchElementException if the file does not exist in the specified bucket
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
   * @throws IllegalArgumentException if key, bucket, or dir is null or empty
   * @throws java.util.NoSuchElementException if the file does not exist in the specified location
   */
  Path download(String key, String bucket, String dir) throws IOException;

  /**
   * Checks if an object exists in the specified cloud storage bucket and directory.
   *
   * @param key the unique identifier/key of the object to check
   * @param bucket the name of the cloud storage bucket to check
   * @param dir the directory path within the bucket where the object should be located
   * @return true if the object exists, false otherwise
   * @throws IllegalArgumentException if key, bucket, or dir is null or empty
   * @throws IOException if an error occurs while checking the object existence
   */
  boolean doesObjectExist(String key, String bucket, String dir);

  /**
   * Generates a presigned URL for accessing a file in cloud storage without authentication.
   * The URL will be valid for the specified duration.
   *
   * @param validity the time unit for the URL validity period (e.g., TimeUnit.HOURS, TimeUnit.DAYS)
   * @param validityValue the numeric value for the validity period in the specified time unit
   * @param key the unique identifier/key of the file in the cloud storage
   * @param bucket the name of the cloud storage bucket containing the file
   * @param dir the directory path within the bucket where the file is located
   * @return a presigned URL that allows temporary access to the file
   * @throws IllegalArgumentException if any parameter is null, empty, or invalid
   * @throws IOException if an error occurs while generating the presigned URL
   * @throws java.util.NoSuchElementException if the file does not exist in the specified location
   */
  URL getPresignedURL(TimeUnit validity, Integer validityValue, String key, String bucket,
      String dir);

}
