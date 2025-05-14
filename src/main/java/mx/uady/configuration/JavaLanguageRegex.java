package mx.uady.configuration;

public class JavaLanguageRegex {
  public static final String ACCESS_MODIFIERS_REGEX_PATTERN = "((public|private|protected)\\s+)?";
  public static final String COMMENTS_REGEX_PATTERN = "(?s)/\\*.*?\\*/|//[^\n]*";
  public static final String DATATYPE_DECLARATION_REGEX_PATTERN =
      "(\\s*[a-zA-Z0-9]+(<[a-zA-Z0-9]+>)?\\s+)";
  public static final String FINAL_OR_STATIC_REGEX_PATTERN =
      "(?:(?:static\\s+)?(?:final\\s+)?|(?:final\\s+)?(?:static\\s+)?)?";
  public static final String IDENTIFIER_DECLARATION_REGEX_PATTERN = "\\w+\\s*";
  public static final String TYPE_KEYS_REGEX_PATTERN = "((class|enum|interface)\\s+)";

  private JavaLanguageRegex() {}
}
