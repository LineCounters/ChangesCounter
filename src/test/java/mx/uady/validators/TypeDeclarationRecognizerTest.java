package mx.uady.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mx.uady.recognizers.TypeDeclarationRecognizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TypeDeclarationRecognizerTest {
  private TypeDeclarationRecognizer typeDeclarationRecognizer;

  @BeforeEach
  void setUp() {
    typeDeclarationRecognizer = new TypeDeclarationRecognizer();
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración de clase")
  void testValidateTypeWithValidClassDeclaration() {
    String validClassDeclaration = "public class MyClass {";

    assertTrue(typeDeclarationRecognizer.recognizes(validClassDeclaration));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración de interfaz")
  void testValidateTypeWithValidInterfaceDeclaration() {
    String validInterfaceDeclaration = "public interface MyInterface {";

    assertTrue(typeDeclarationRecognizer.recognizes(validInterfaceDeclaration));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración de enum")
  void testValidateTypeWithValidEnumDeclaration() {
    String validEnumDeclaration = "public enum MyEnum {";

    assertTrue(typeDeclarationRecognizer.recognizes(validEnumDeclaration));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración de método")
  void testValidateTypeWithInvalidDeclaration() {
    String invalidDeclaration = "public void myMethod() {";

    assertFalse(typeDeclarationRecognizer.recognizes(invalidDeclaration));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración con linea vacía")
  void testValidateTypeWithEmptyLine() {
    String emptyLine = "";

    assertFalse(typeDeclarationRecognizer.recognizes(emptyLine));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración con espacios en blanco")
  void testValidateTypeWithWhitespaceOnly() {
    String whitespaceOnly = "    ";

    assertFalse(typeDeclarationRecognizer.recognizes(whitespaceOnly));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración con comentario de bloque")
  void testValidateTypeWithCommentLine() {
    String commentLine = "// This is a comment";

    assertFalse(typeDeclarationRecognizer.recognizes(commentLine));
  }

  @Test
  @DisplayName("Debe de validar correctamente una declaración parcial")
  void testValidateTypeWithPartialDeclaration() {
    String partialDeclaration = "class";

    assertFalse(typeDeclarationRecognizer.recognizes(partialDeclaration));
  }
}
