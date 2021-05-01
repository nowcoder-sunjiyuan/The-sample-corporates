package com.sjiyuan.niu.param;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件的相关参数
 * @author JiYuan Sun
 * @date 2021/05/01
 */
@Component
@ConfigurationProperties(prefix="file-setting")
public class FileSetting {
  private String rootPath;
  private String targetPath;
  private String taskClassification;
  private String taskTarget;

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  public String getTargetPath() {
    return targetPath;
  }

  public void setTargetPath(String targetPath) {
    this.targetPath = targetPath;
  }

  public String getTaskClassification() {
    return taskClassification;
  }

  public void setTaskClassification(String taskClassification) {
    this.taskClassification = taskClassification;
  }

  public String getTaskTarget() {
    return taskTarget;
  }

  public void setTaskTarget(String taskTarget) {
    this.taskTarget = taskTarget;
  }
}
