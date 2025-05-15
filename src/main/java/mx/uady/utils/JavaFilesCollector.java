package mx.uady.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mx.uady.exceptions.FolderNotFoundException;
import mx.uady.exceptions.JavaFilesNotFoundInPathException;

public class JavaFilesCollector {
  /**
   * Método para obtener los archivos .java dentro de un directorio y sus subdirectorios.
   *
   * @param folderPath Ruta del directorio raíz.
   * @return Mapa con la ruta relativa del archivo como clave y la ruta completa como valor.
   * @throws FolderNotFoundException Si la carpeta no existe o no es válida.
   * @throws JavaFilesNotFoundInPathException Si no se encuentran archivos .java en la carpeta.
   */
  public static Map<String, Path> getJavaFilePathsByFolderPath(String folderPath)
      throws FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path rootPath = Paths.get(folderPath);

    try (Stream<Path> stream = Files.walk(rootPath)) {
      Map<String, Path> javaFilesMap =
          stream
              .filter(Files::isRegularFile)
              .filter(path -> path.toString().endsWith(".java"))
              .collect(
                  Collectors.toMap(path -> rootPath.relativize(path).toString(), path -> path));

      if (javaFilesMap.isEmpty()) {
        throw new JavaFilesNotFoundInPathException();
      }

      return javaFilesMap;
    } catch (IOException e) {
      throw new FolderNotFoundException(folderPath);
    }
  }

  private JavaFilesCollector() {}
}
