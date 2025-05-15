package mx.uady.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CodeAnalyzerTest {

  private CodeMetricsManager structCounter;
  private CodeAnalyzer codeAnalyzer;

  @BeforeEach
  void setUp() {
    structCounter = new CodeMetricsManager();
    codeAnalyzer = new CodeAnalyzer(structCounter);
  }

  @Test
  @DisplayName("Procesar línea: línea de comentario")
  void testProcessLine_CommentLine() {
    String line = "// Esto es un comentario";

    codeAnalyzer.processLine(line);

    assertTrue(structCounter.getClasses().isEmpty());
  }

  @Test
  @DisplayName("Procesar línea: línea vacía")
  void testProcessLine_EmptyLine() {
    String line = "   ";

    codeAnalyzer.processLine(line);

    assertTrue(structCounter.getClasses().isEmpty());
  }

  @Test
  @DisplayName("Preprocesar línea: ignorar línea vacía")
  void testPreprocessLine_IgnoreEmptyLine() {
    String emptyLine = "   ";

    String result = codeAnalyzer.preprocessLine(emptyLine);

    assertNull(result);
  }

  @Test
  @DisplayName("Preprocesar línea: línea válida")
  void testPreprocessLine_ValidLine() {
    String validLine = "public class TestClass {";

    String result = codeAnalyzer.preprocessLine(validLine);

    assertEquals("public class TestClass {", result);
  }

  @Test
  @DisplayName("Contar métodos en una clase")
  void testCountMethodsInClass() {
    String line1 = "public class TestClass {";
    String line2 = "public void testMethod() {";
    String line3 = "return; } }";

    codeAnalyzer.processLine(line1);
    codeAnalyzer.processLine(line2);
    codeAnalyzer.processLine(line3);

    assertEquals(1, structCounter.getClasses().get(0).getMethodsCount());
  }

  @Test
  @DisplayName("Contar líneas de código en una clase")
  void testCountLinesOfCodeInClass() {
    String line1 = "public class TestClass {";
    String line2 = "int a = 0;";
    String line3 = "return; } }";

    codeAnalyzer.processLine(line1);
    codeAnalyzer.processLine(line2);
    codeAnalyzer.processLine(line3);

    assertEquals(3, structCounter.getClasses().get(0).getPhysicalLinesCount());
  }
}
