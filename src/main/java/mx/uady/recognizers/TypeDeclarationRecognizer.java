package mx.uady.recognizers;

import static mx.uady.configuration.JavaLanguageRegex.ACCESS_MODIFIERS_REGEX_PATTERN;
import static mx.uady.configuration.JavaLanguageRegex.TYPE_KEYS_REGEX_PATTERN;

/**
 * Clase que verifica si existe una declaración de tipo (class, interface, enum) dentro de una línea
 * de código.
 */
public class TypeDeclarationRecognizer implements CodeStructureRecognizer {
  private boolean matchesTypeDeclaration(String codeLine) {
    String typeDeclarationPattern =
        "^(\\s*" + ACCESS_MODIFIERS_REGEX_PATTERN + ".*\\s*" + TYPE_KEYS_REGEX_PATTERN + ".*)";

    return codeLine.matches(typeDeclarationPattern) && !codeLine.contains("\"");
  }

  @Override
  public boolean recognizes(String codeLine) {
    return matchesTypeDeclaration(codeLine);
  }

  public String getTypeIdentifier(String codeLine) {
    if (codeLine.contains("\"")) {
      return null;
    }

    String[] lineTokens = codeLine.trim().split("\\s+");

    for (int tokenIterator = 0; tokenIterator < lineTokens.length - 1; tokenIterator++) {
      if (lineTokens[tokenIterator].equals("class")
          || lineTokens[tokenIterator].equals("interface")
          || lineTokens[tokenIterator].equals("enum")) {
        return lineTokens[tokenIterator + 1];
      }
    }

    return null;
  }
}
