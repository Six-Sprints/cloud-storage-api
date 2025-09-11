# Cloud Storage Service

The `CloudStorage` interface (`com.sixsprints.cloudservice.service.CloudStorage`) provides a standardized way to interact with cloud file storage services (like AWS S3 or Google Cloud Storage). It abstracts the underlying implementation, allowing you to perform common operations like uploading, downloading, and managing files with a consistent API.

---

## Uploading Files 📤

These methods are used to upload new files to your cloud storage bucket. The file data is passed using a `FileDto` (`com.sixsprints.cloudservice.dto.FileDto`) object, which can be constructed from a `byte[]` array or a `java.io.File`.

### `upload`

This is the standard method for uploading a file.

**Signature**

```java
String upload(FileDto fileDto, String bucket);
```

**Description**
Takes a `FileDto` and uploads it to the specified bucket. It returns the URL of the uploaded file that you can use to access the file directly.

- `fileDto`: An object containing the `fileName` and either the file content as `bytes` or a `fileToUpload` object.
- `bucket`: The name of the cloud storage bucket to upload to.

**Example**

```java
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final CloudStorage cloudStorage;

    public String saveUserProfilePicture(MultipartFile file, String bucketName) throws IOException {
        // Build the DTO from the uploaded file's bytes
        FileDto fileDto = FileDto.builder()
            .fileName(file.getOriginalFilename())
            .bytes(file.getBytes())
            .build();

        String fileUrl = cloudStorage.upload(fileDto, bucketName);
        // Store the returned 'fileUrl' in your database
        return fileUrl;
    }
}
```

---

### `resizeAndUpload`

This is a specialized method for uploading **images**. It first resizes the image to fit within a maximum dimension (while preserving the aspect ratio) and then uploads it.

**Signature**

```java
String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);
```

**Description**
Useful for creating thumbnails or ensuring images don't exceed a certain size. Returns the URL of the uploaded resized image.

- `fileDto`: The image file to be resized and uploaded.
- `bucket`: The name of the cloud storage bucket.
- `maxImageSize`: The maximum width or height (in pixels) for the resized image.

**Example**

```java
public String uploadThumbnail(MultipartFile imageFile, String bucketName) throws IOException {
    FileDto imageDto = FileDto.builder()
        .fileName(imageFile.getOriginalFilename())
        .bytes(imageFile.getBytes())
        .build();

    // Resize the image to a max of 800px width/height and upload it.
    String thumbnailUrl = cloudStorage.resizeAndUpload(imageDto, bucketName, 800.0);
    return thumbnailUrl;
}
```

---

## Downloading Files 📥

These methods are used to download files from your cloud storage to the local filesystem.

### `download(key, bucket)`

Downloads a file from the root of a bucket to a temporary local file.

**Signature**

```java
Path download(String key, String bucket) throws IOException;
```

**Description**

- `key`: The unique key of the file you want to download.
- `bucket`: The name of the bucket where the file is stored.

**Example**

```java
// Download the file to a temporary location on the server
Path localFilePath = cloudStorage.download("a1b2-c3d4-e5f6", "my-file-bucket");
File tempFile = localFilePath.toFile();
// You can now process 'tempFile'
```

---

### `download(key, bucket, dir)`

Downloads a file from a specific directory within a bucket.

**Signature**

```java
Path download(String key, String bucket, String dir) throws IOException;
```

**Description**

- `key`: The unique key of the file.
- `bucket`: The name of the bucket.
- `dir`: The directory path inside the bucket where the file is located.

**Example**

```java
// Downloads a file from the 'invoices/2025/' directory
Path localInvoicePath = cloudStorage.download("inv-001.pdf", "company-files", "invoices/2025/");
```

---

## Checking File Existence 🔎

This method allows you to verify if a file exists without having to download it.

### `doesObjectExist`

**Signature**

```java
boolean doesObjectExist(String key, String bucket);
```

**Description**
Returns `true` if the specified object exists in the given bucket, and `false` otherwise.

- `key`: The key of the file to check.
- `bucket`: The name of the bucket.

**Example**

```java
boolean fileExists = cloudStorage.doesObjectExist("report.csv", "analytics-bucket");
if (fileExists) {
    // Proceed with processing
} else {
    // Handle missing file
}
```

---

## Generating Presigned URLs 🔗

A presigned URL is a temporary, secure link to a private file in your cloud storage. Anyone with the URL can access the file for a limited time without needing any credentials.

### `getPresignedURL`

**Signature**

```java
URL getPresignedURL(TimeUnit validity, Integer validityValue, String key, String bucket);
```

**Description**
Generates a URL that provides temporary access to a file.

- `validity`: The unit of time for the URL's expiration (e.g., `TimeUnit.MINUTES`, `TimeUnit.HOURS`).
- `validityValue`: The duration for which the URL is valid (e.g., `10` for 10 minutes).
- `key`: The key of the file.
- `bucket`: The name of the bucket.

**Example**

```java
// Generate a URL for 'document.pdf' that is valid for 1 hour
URL temporaryUrl = cloudStorage.getPresignedURL(
    TimeUnit.HOURS,
    1,
    "document.pdf",
    "secure-documents"
);
// This 'temporaryUrl' can be sent to a user for them to download the file directly.
String urlString = temporaryUrl.toString();
```

---

## FileDto Structure

The `FileDto` class is used to pass file information to the cloud storage methods:

```java
@Data
@Builder
public class FileDto {
    private String fileName;        // The name of the file
    private File fileToUpload;      // A File object to upload
    private byte[] bytes;           // File content as byte array
}
```

**Note**: You can provide either `fileToUpload` or `bytes` - not both. The `fileName` is always required.

---

## Exception Handling

The following exceptions may be thrown by the CloudStorage methods:

- `IOException`: When I/O errors occur during file operations (download methods only)
- `IllegalArgumentException`: When required parameters are null, empty, or invalid (getPresignedURL only)

**Example with proper exception handling:**

```java
public String safeUpload(MultipartFile file, String bucketName) {
    try {
        FileDto fileDto = FileDto.builder()
            .fileName(file.getOriginalFilename())
            .bytes(file.getBytes())
            .build();

        return cloudStorage.upload(fileDto, bucketName);
    } catch (Exception e) {
        log.error("Failed to upload file", e);
        throw new ServiceException("File upload failed");
    }
}

public Path safeDownload(String key, String bucket) {
    try {
        return cloudStorage.download(key, bucket);
    } catch (IOException e) {
        log.error("Failed to download file", e);
        throw new ServiceException("File download failed");
    }
}
```
