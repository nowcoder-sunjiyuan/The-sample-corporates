package com.sjiyuan.niu.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计文章中字符出现的次数
 *
 * @author JiYuan Sun
 * @date 2021/08/30
 */
@RestController
@Slf4j
public class CountWordController {

  public void countWord() throws IOException {
    String filePath = "/Users/nowcoder/Downloads/牛一琳/08.30/";

    Collection<File> source = FileUtils.listFiles(new File(filePath),
        new String[]{"txt"}, true);

    source.parallelStream().forEach(file -> {
      try {
        Map<String, Integer> result = countWordSingleFile(file);
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
          // 将map写入到txt
          FileUtils.writeStringToFile(new File(file.getName()),
              entry.getKey() + " : " + entry.getValue() + "\n", "UTF-8", true);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * 统计一个文件中的所有字符，输出到txt中
   */
  public Map<String, Integer> countWordSingleFile(File file) throws IOException {

    List<String> wordList = FileUtils.readLines(new File("src/main/resources/wordlist.txt"), "UTF"
        + "-8");
    Map<String, Integer> result = new LinkedHashMap<>();
    for (String word : wordList) {
      log.info("word:" + word);
      int count = countSingleWordSingleFile(word, file);
      result.put(word, count);
    }
    return result;
  }

  /**
   * 统计file里面的word的次数
   *
   * @param regex
   * @param file
   */
  public int countSingleWordSingleFile(String regex, File file) throws IOException {
    String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());

    Pattern compile = Pattern.compile(regex);
    int count = 0;
    Matcher matcher = compile.matcher(fileContent);
    while(matcher.find()) {
      count++;
    }
    return count;
  }

  public static void main(String[] args) {

    CountWordController countWordController = new CountWordController();
    try {
      countWordController.countWord();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
