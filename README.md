# Cloud Storage API

A Java library that provides a unified interface for cloud storage operations across multiple cloud providers including AWS S3 and Google Cloud Storage. This library simplifies file upload, download, and management operations with built-in image resizing capabilities.

## Features

- **Multi-Cloud Support**: Works with AWS S3 and Google Cloud Storage
- **Unified Interface**: Single API for different cloud storage providers
- **Image Processing**: Built-in image resizing functionality
- **File Management**: Upload, download, and check file existence
- **Presigned URLs**: Generate temporary access URLs for files
- **Flexible Input**: Support for both File objects and byte arrays
- **Java 17+**: Modern Java features and performance

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.sixsprints</groupId>
    <artifactId>cloud-service</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.sixsprints:cloud-service:2.0.0'
```

## Quick Start

### AWS S3 Setup

```java
import com.sixsprints.cloudservice.dto.Credentials;
import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.impl.S3CloudStorage;
import software.amazon.awssdk.regions.Region;

// Create credentials
Credentials credentials = Credentials.builder()
    .accessId("your-access-key")
    .secretKey("your-secret-key")
    .region(Region.US_EAST_1)
    .build();

// Initialize S3 storage service
S3CloudStorage s3Storage = new S3CloudStorage(credentials);

// Upload a file
FileDto fileDto = FileDto.builder()
    .fileName("example.jpg")
    .fileToUpload(new File("path/to/your/file.jpg"))
    .build();

String fileKey = s3Storage.upload(fileDto, "your-bucket-name");
```

### Google Cloud Storage Setup

```java
import com.sixsprints.cloudservice.dto.Credentials;
import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.impl.GoogleCloudStorage;
import java.io.FileInputStream;

// Create credentials
Credentials credentials = Credentials.builder()
    .file(new FileInputStream("path/to/service-account-key.json"))
    .projectId("your-project-id")
    .build();

// Initialize Google Cloud Storage service
GoogleCloudStorage gcsStorage = new GoogleCloudStorage(credentials);

// Upload a file
FileDto fileDto = FileDto.builder()
    .fileName("example.jpg")
    .fileToUpload(new File("path/to/your/file.jpg"))
    .build();

String fileKey = gcsStorage.upload(fileDto, "your-bucket-name");
```

## API Reference

### CloudStorage Interface

The main interface that all cloud storage implementations must follow:

#### Upload Operations

```java
// Basic file upload
String upload(FileDto fileDto, String bucket);

// Upload with image resizing
String resizeAndUpload(FileDto fileDto, String bucket, Double maxImageSize);
```

#### Download Operations

```java
// Download to temporary location
Path download(String key, String bucket) throws IOException;

// Download to specific directory
Path download(String key, String bucket, String dir) throws IOException;
```

#### File Management

```java
// Check if file exists
boolean doesObjectExist(String key, String bucket, String dir);

// Generate presigned URL for temporary access
URL getPresignedURL(TimeUnit validity, Integer validityValue, String key, String bucket, String dir);
```

### Data Transfer Objects

#### FileDto

```java
FileDto fileDto = FileDto.builder()
    .fileName("example.jpg")                    // File name
    .fileToUpload(new File("path/to/file"))     // File object (optional)
    .bytes(fileBytes)                          // Byte array (optional)
    .build();
```

#### Credentials

```java
// For AWS S3
Credentials awsCredentials = Credentials.builder()
    .accessId("your-access-key")
    .secretKey("your-secret-key")
    .region(Region.US_EAST_1)
    .build();

// For Google Cloud Storage
Credentials gcsCredentials = Credentials.builder()
    .file(new FileInputStream("service-account.json"))
    .projectId("your-project-id")
    .build();
```

## Advanced Usage

### Image Resizing

The library includes built-in image resizing functionality that maintains aspect ratio:

```java
// Resize image to maximum 800px width/height and upload
String resizedImageKey = storage.resizeAndUpload(
    fileDto,
    "my-bucket",
    800.0
);
```

### Presigned URLs

Generate temporary access URLs for files:

```java
// Generate URL valid for 1 hour
URL presignedUrl = storage.getPresignedURL(
    TimeUnit.HOURS,
    1,
    "file-key",
    "bucket-name",
    "directory-path"
);
```

### File Existence Check

```java
boolean exists = storage.doesObjectExist(
    "file-key",
    "bucket-name",
    "directory-path"
);
```

## Error Handling

The library throws appropriate exceptions for different error conditions:

- `IllegalArgumentException`: Invalid parameters or configuration
- `IOException`: I/O errors during file operations
- `UnsupportedOperationException`: Unsupported file formats for image operations
- `java.util.NoSuchElementException`: File not found

## Requirements

- Java 17 or higher
- Maven 3.6+ or Gradle 6.0+

## Dependencies

- **AWS SDK for Java 2.x**: For S3 operations
- **Google Cloud Storage**: For Google Cloud Storage operations
- **Lombok**: For reducing boilerplate code
- **JUnit 5**: For testing

## Testing

Run the test suite:

```bash
mvn test
```

The project includes comprehensive tests for both S3 and Google Cloud Storage implementations.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:

- Create an issue in the repository
- Check the documentation
- Review the test cases for usage examples

## Changelog

### Version 2.0.0

- Updated to Java 17
- Enhanced image resizing capabilities
- Improved error handling
- Added comprehensive Javadoc documentation
- Updated dependencies to latest versions

### Version 1.1.3

- Initial release with basic cloud storage functionality
- Support for AWS S3 and Google Cloud Storage
- Image resizing features
- Presigned URL generation
