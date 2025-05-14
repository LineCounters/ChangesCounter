package mx.uady.recognizers;

import static mx.uady.configuration.JavaLanguageRegex.ACCESS_MODIFIERS_REGEX_PATTERN;
import static mx.uady.configuration.JavaLanguageRegex.DATATYPE_DECLARATION_REGEX_PATTERN;
import static mx.uady.configuration.JavaLanguageRegex.FINAL_OR_STATIC_REGEX_PATTERN;
import static mx.uady.configuration.JavaLanguageRegex.IDENTIFIER_DECLARATION_REGEX_PATTERN;

/** Clase que verifica si existe la declaración de un método en una línea de código. */
public class MethodDeclarationRecognizer implements CodeStructureRecognizer {
  private boolean matchesMethodDefinition(String codeLineToCheck) {
    String methodDeclarationPattern =
        "^(\\s*"
            + ACCESS_MODIFIERS_REGEX_PATTERN
            + FINAL_OR_STATIC_REGEX_PATTERN
            + DATATYPE_DECLARATION_REGEX_PATTERN
            + IDENTIFIER_DECLARATION_REGEX_PATTERN
            + "\\(.*)";

    return codeLineToCheck.matches(methodDeclarationPattern)
        && !codeLineToCheck.contains(";")
        && !codeLineToCheck.contains("\"");
  }

  @Override
  public boolean recognizes(String codeLine) {
    return matchesMethodDefinition(codeLine);
  }
}
