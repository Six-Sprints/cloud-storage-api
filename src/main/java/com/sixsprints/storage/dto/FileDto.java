package com.sixsprints.storage.dto;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {

  private String fileName;

  private File fileToUpload;

}
