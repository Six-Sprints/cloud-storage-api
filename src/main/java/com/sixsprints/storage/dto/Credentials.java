package com.sixsprints.storage.dto;

import java.io.InputStream;

import com.amazonaws.regions.Regions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {

  private InputStream file;

  private String projectId;

  private String accessId;

  private String secretKey;

  private Regions region;

}
