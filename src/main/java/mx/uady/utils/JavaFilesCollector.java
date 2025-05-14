package mx.uady.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import mx.uady.exceptions.FolderNotFoundException;
import mx.uady.exceptions.JavaFilesNotFoundInPathException;

public class JavaFilesCollector {
  /**
   * Método para obtener los archivos .java dentro de un directorio y sus subdirectorios.
   *
   * @param folderPath Ruta del directorio raíz.
   * @return Lista de rutas de archivos .java encontrados.
   * @throws FolderNotFoundException Si la carpeta no existe o no es válida.
   * @throws JavaFilesNotFoundInPathException Si no se encuentran archivos .java en la carpeta.
   */
  public static List<Path> getJavaFilePathsByFolderPath(String folderPath)
      throws FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path path = Paths.get(folderPath);

    try (Stream<Path> stream = Files.walk(path)) {
      List<Path> javaFiles =
          stream
              .filter(Files::isRegularFile)
              .filter(fileName -> fileName.toString().endsWith(".java"))
              .toList();

      if (javaFiles.isEmpty()) {
        throw new JavaFilesNotFoundInPathException();
      }

      return javaFiles;
    } catch (IOException e) {
      throw new FolderNotFoundException(folderPath);
    }
  }

  private JavaFilesCollector() {}
}
