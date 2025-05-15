package mx.uady.exceptions;

public class FolderNotFoundException extends Exception {
  public FolderNotFoundException(String folderPath) {
    super(
        "No se ha encontrado la carpeta o la ruta no corresponde a una carpeta %s"
            .formatted(folderPath));
  }
}
