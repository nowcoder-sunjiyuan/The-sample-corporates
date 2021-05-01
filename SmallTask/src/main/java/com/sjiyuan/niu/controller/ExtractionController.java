package com.sjiyuan.niu.controller;

import com.sjiyuan.niu.param.FileSetting;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author JiYuan Sun
 * @date 2021/05/01
 */
@RestController
public class ExtractionController {

  private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
  @Autowired
  FileSetting fileSetting;

  @GetMapping("/pdf_to_txt")
  public boolean fun() {
    return pdfToTxt();
  }

  @GetMapping("/task_classification")
  public boolean taskClassification() {
    Collection<File> source = FileUtils.listFiles(new File(fileSetting.getTaskClassification()),
        new String[]{"txt"}, true);
    int i = 0;
    File file1 = new File(fileSetting.getTaskTarget() + "制造业.txt");
    File file2 = new File(fileSetting.getTaskTarget() + "能源业.txt");
    try {
      FileUtils.touch(file1);
      FileUtils.touch(file2);

      for (File file : source) {
        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        if (file.getParentFile().getName().equals("制造业")) {
          FileUtils.writeStringToFile(file1, fileContent, Charset.defaultCharset(), true);
          System.out.println("添加制造业文件，第" + i++ + "个");
        } else if (file.getParentFile().getName().equals("能源业")) {
          FileUtils.writeStringToFile(file2, fileContent, Charset.defaultCharset(), true);
          System.out.println("添加能源业文件，第" + i++ + "个");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  @GetMapping("/task_year")
  public boolean taskYear() {
    Collection<File> source = FileUtils.listFiles(new File(fileSetting.getTaskClassification()),
        new String[]{"txt"}, true);
    int i = 0;
    try {
      for (File file : source) {
        String fileName = file.getName();
        String target;
        if (fileName.contains(" ")) {
          String[] s = fileName.trim().split(" ");
          target = s[0];
        } else if(fileName.contains("-")){
          String[] s = fileName.trim().split("-");
          target = s[0];
        }else {
          String[] s = fileName.trim().split("_");
          target = s[0];
        }

        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        File targetFile = new File(fileSetting.getTaskTarget() + target);
        FileUtils.touch(targetFile);

        FileUtils.writeStringToFile(targetFile, fileContent, Charset.defaultCharset(), true);
        System.out.println("第" + i++ + "个文件：" + file.getName() + "，目标" + targetFile.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }


  public boolean pdfToTxt() {
    String rootPath = fileSetting.getRootPath();
    //String targetPath = fileSetting.getTargetPath();

    Collection<File> source = FileUtils.listFiles(new File(rootPath),
        new String[]{"pdf"}, true);
    int i = 0;
    for (File file : source) {
      i++;
      try {
        //AutoDetectParser自动检测解析器（自动类型检测的功能）
        Parser parser = new AutoDetectParser();
        //处理类对象
        BodyContentHandler handler = new BodyContentHandler(100 * 1024 * 1024);
        //元数据
        Metadata metadata = new Metadata();
        FileInputStream fileInputStream = new FileInputStream(file);
        //上下文对象
        ParseContext parseContext = new ParseContext();
        parser.parse(fileInputStream, handler, metadata, parseContext);
        String fileContent = handler.toString();
        System.out.println("----------完成" + i + "个文件");

        //String result = fileContent.replaceAll(REGEX_CHINESE, "");
        //System.out.println(result);
        //writeTxt(result, newPath);

        // 将文件中的内容写进对应的txt当中
        File newFile = new File(
            file.getParentFile().getParent().replace(
                "Downloads", "Downloads" + "/target") + "/" +
                file.getName().replace("pdf", "txt"));
        /*System.out.println(file.getParent());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getName());*/
        System.out.println(newFile.getAbsolutePath());
        FileUtils.touch(newFile);
        /*if(!newFile.exists()){
          newFile.createNewFile();
        }*/
        FileUtils.writeStringToFile(newFile, fileContent, Charset.defaultCharset(), true);
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  /**
   * 将多个txt合并(年份)
   */
  /*public void fun() {
    folderListTxt("D:\\laji\\中国企业txt\\");
  }

  public void folderListTxt(String path) {
    File file = new File(path);
    if (file.exists()) {
      File[] files = file.listFiles();
      if (null != files) {
        for (File file2 : files) {
          if (file2.isDirectory()) {
            //System.out.println("文件夹:" + file2.getAbsolutePath());
            folderListTxt(file2.getAbsolutePath());
          } else {
            //System.out.println("文件:" + file2.getAbsolutePath());
            System.out.println(file2.getParent());
            System.out.println(file2.getName());
            //extraction(file2.getAbsolutePath(),file2.getAbsolutePath().replace("pdf","txt"));
            writeTxtYear(file2.getAbsolutePath(), "D:\\laji\\年份\\" + file2.getName().split(" ")[0] + ".txt");
          }
        }
      }
    } else {
      System.out.println("文件不存在!");
    }
  }

  public void writeTxtYear(String oldFilename, String filePath) {

    File old = new File(oldFilename);
    File txt = new File(filePath);

    try {
      FileReader fr = new FileReader(old);
      BufferedReader br = new BufferedReader(fr);
      if (!txt.exists()) {
        txt.createNewFile();
      }

      BufferedWriter bw = new BufferedWriter(new FileWriter(txt,true));
      String line;
      while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.newLine();
      }
      br.close();
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }*/

  /**
   * 分类
   */
  /*public void fun2(){
    folderListClassification("D:\\laji\\中国企业txt\\制造业\\", "制造业.txt");

    System.out.println("-------------------------------------------------------分割线");
    folderListClassification("D:\\laji\\中国企业txt\\能源业\\", "能源业.txt");
  }

  public void folderListClassification(String path, String newFileName) {
    File file = new File(path);
    if (file.exists()) {
      File[] files = file.listFiles();
      if (null != files) {
        for (File file2 : files) {
          if (file2.isDirectory()) {
            //System.out.println("文件夹:" + file2.getAbsolutePath());
            folderListClassification(file2.getAbsolutePath(), newFileName);
          } else {
            //System.out.println("文件:" + file2.getAbsolutePath());
            System.out.println(file2.getParent());
            System.out.println(file2.getName());
            //extraction(file2.getAbsolutePath(),file2.getAbsolutePath().replace("pdf","txt"));
            writeTxtYear(file2.getAbsolutePath(), "D:\\laji\\分类\\" + newFileName);
          }
        }
      }
    } else {
      System.out.println("文件不存在!");
    }
  }
*/


}
