package com.sixsprints.cloudservice.dto;

import java.io.InputStream;

import software.amazon.awssdk.regions.Region;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {

  private InputStream file;

  private String projectId;

  private String accessId;

  private String secretKey;

  private Region region;

}
