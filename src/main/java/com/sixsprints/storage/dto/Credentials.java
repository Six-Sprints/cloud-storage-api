package com.sixsprints.storage.dto;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {

  private File file;

  private String projectId;

}
