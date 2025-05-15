package mx.uady.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MethodDeclarationRecognizerTest {
  private MethodDeclarationRecognizer methodDeclarationRecognizer;

  @BeforeEach
  void setUp() {
    methodDeclarationRecognizer = new MethodDeclarationRecognizer();
  }

  @Test
  @DisplayName("Debe retornar true para una declaración de método válida")
  void testValidateType_ValidMethodDeclaration() {
    String validMethod = "public static void main(String[] args)";

    assertTrue(
        methodDeclarationRecognizer.recognizes(validMethod),
        "Should return true for a valid method declaration");
  }

  @Test
  @DisplayName("Debe retornar true para una declaración de método válida sin modificadores")
  void testValidateType_ValidMethodWithoutModifiers() {
    String validMethod = "void doSomething()";

    assertTrue(
        methodDeclarationRecognizer.recognizes(validMethod),
        "Should return true for a valid method declaration without modifiers");
  }

  @Test
  @DisplayName("Debe retornar false para una declaración de método con punto y coma")
  void testValidateType_InvalidMethodWithSemicolon() {
    String invalidMethod = "public void doSomething();";

    assertFalse(
        methodDeclarationRecognizer.recognizes(invalidMethod),
        "Should return false for a method declaration with a semicolon");
  }

  @Test
  @DisplayName("Debe retornar false para una línea de código que no es una declaración de método")
  void testValidateType_InvalidLineOfCode() {
    String invalidCode = "int x = 0;";

    assertFalse(
        methodDeclarationRecognizer.recognizes(invalidCode),
        "Should return false for a non-method declaration line of code");
  }

  @Test
  @DisplayName("Debe retornar false para una línea vacía")
  void testValidateType_EmptyLine() {
    String emptyLine = "";

    assertFalse(
        methodDeclarationRecognizer.recognizes(emptyLine), "Should return false for an empty line");
  }

  @Test
  @DisplayName("Debe retornar false para una línea con solo espacios en blanco")
  void testValidateType_WhitespaceOnly() {
    String whitespaceOnly = "   ";

    assertFalse(
        methodDeclarationRecognizer.recognizes(whitespaceOnly),
        "Should return false for a line with only whitespace");
  }

  @Test
  @DisplayName("Debe retornar false para una declaración de método sin paréntesis")
  void testValidateType_InvalidMethodWithoutParentheses() {
    String invalidMethod = "public void doSomething";

    assertFalse(
        methodDeclarationRecognizer.recognizes(invalidMethod),
        "Should return false for a method declaration without parentheses");
  }
}
