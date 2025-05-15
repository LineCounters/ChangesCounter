package mx.uady.utils;

import static mx.uady.configuration.JavaLanguageRegex.COMMENTS_REGEX_PATTERN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFileSanitizer {
  private JavaFileSanitizer() {}

  public static List<String> removeComments(List<String> fileLines) {
    String contentWithoutComments =
        String.join("\n", fileLines).replaceAll(COMMENTS_REGEX_PATTERN, "");

    return new ArrayList<>(Arrays.asList(contentWithoutComments.split("\n")));
  }

  public static List<String> removeBlankLines(List<String> fileLines) {
    return fileLines.stream().filter(line -> !line.isBlank()).collect(Collectors.toList());
  }
}
