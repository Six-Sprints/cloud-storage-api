package com.sixsprints.storage.dto;

import java.io.InputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {

  private InputStream file;

  private String projectId;

}
