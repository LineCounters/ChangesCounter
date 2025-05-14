package mx.uady.recognizers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeStructureRecognizerFactory {
  private static final List<CodeStructureRecognizer> AVAILABLE_RECOGNIZERS =
      Collections.unmodifiableList(
          Arrays.asList(new MethodDeclarationRecognizer(), new TypeDeclarationRecognizer()));

  /**
   * Encuentra el reconocedor adecuado para una línea de código dada
   *
   * @param codeLine La línea de código a analizar
   * @return El reconocedor que puede procesar esta línea, o null si ninguno aplica
   */
  public static CodeStructureRecognizer getRecognizerForCodeLine(String codeLine) {
    if (codeLine == null || codeLine.isBlank()) {
      return null;
    }

    return AVAILABLE_RECOGNIZERS.stream()
        .filter(recognizer -> recognizer.recognizes(codeLine))
        .findFirst()
        .orElse(null);
  }

  private CodeStructureRecognizerFactory() {}
}
