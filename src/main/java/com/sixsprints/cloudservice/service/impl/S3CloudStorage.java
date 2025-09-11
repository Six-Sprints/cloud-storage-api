package com.sixsprints.cloudservice.service.impl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import com.sixsprints.cloudservice.dto.Credentials;
import com.sixsprints.cloudservice.dto.FileDto;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;

public class S3CloudStorage extends AbstractCloudStorageService {

  private final S3Client client;

  private Credentials cred;

  // https://{bucket}.s3.{region}.amazonaws.com/{fileName}
  private static final String BASE_URL = "https://%1$s.s3.%2$s.amazonaws.com/%3$s";

  public S3CloudStorage(Credentials cred) {
    this.cred = cred;
    AwsCredentials credentials =
        AwsBasicCredentials.create(cred.getAccessId(), cred.getSecretKey());
    client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(cred.getRegion()).build();
  }

  @Override
  public String upload(FileDto fileDto, String bucket) {
    client.putObject(PutObjectRequest.builder().bucket(bucket).key(fileDto.getFileName()).build(),
        RequestBody.fromFile(fileDtoToFile(fileDto)));
    return String.format(BASE_URL, bucket, cred.getRegion().toString().toLowerCase(),
        fileDto.getFileName());
  }

  @Override
  public Path download(String key, String bucket, String dir) throws IOException {
    Path outputFile = createTempFile(key, dir);
    ResponseInputStream<GetObjectResponse> s3object =
        client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
    Files.copy(s3object, outputFile);
    return outputFile;
  }

  @Override
  public boolean doesObjectExist(String key, String bucket, String dir) {
    try {
      return client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build())
          .sdkHttpResponse().isSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public URL getPresignedURL(TimeUnit validity, Integer validityValue, String key, String bucket,
      String dir) {

    if (validityValue == null) {
      validityValue = 30;
    }

    // Convert TimeUnit to Duration
    Duration duration = Duration.of(validityValue, validity.toChronoUnit());

    // Create S3Presigner with the same credentials and region as the S3Client
    AwsCredentials credentials =
        AwsBasicCredentials.create(cred.getAccessId(), cred.getSecretKey());
    S3Presigner presigner =
        S3Presigner.builder().credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(cred.getRegion()).build();

    try {
      // Create GetObjectRequest
      GetObjectRequest getObjectRequest =
          GetObjectRequest.builder().bucket(bucket).key(key).build();

      // Create presigned request
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .getObjectRequest(getObjectRequest).signatureDuration(duration).build();

      // Generate presigned URL
      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

      return presignedRequest.url();
    } finally {
      // Close the presigner to free resources
      presigner.close();
    }
  }

}
