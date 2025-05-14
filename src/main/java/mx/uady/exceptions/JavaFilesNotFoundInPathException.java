package mx.uady.exceptions;

public class JavaFilesNotFoundInPathException extends Exception {
  public JavaFilesNotFoundInPathException() {
    super("No se han encontrado archivos Java por analizar en la carpeta del proyecto");
  }
}
