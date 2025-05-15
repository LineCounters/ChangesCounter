package mx.uady.usecases;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.uady.utils.JavaFilesCollector;
import mx.uady.utils.LevenshteinDistance;

public class ProjectVersionComparision {
  public static void compareProjectVersions(
      String oldVersionFolderPath, String newVersionFolderPath) throws Exception {
    Map<String, Path> oldVersionFiles =
        JavaFilesCollector.getJavaFilePathsByFolderPath(oldVersionFolderPath);
    Map<String, Path> newVersionFiles =
        JavaFilesCollector.getJavaFilePathsByFolderPath(newVersionFolderPath);

    Set<String> relativePathsSet = new HashSet<>();
    relativePathsSet.addAll(oldVersionFiles.keySet());
    relativePathsSet.addAll(newVersionFiles.keySet());

    for (String relativePath : relativePathsSet) {
      Path oldVersionFilePath = oldVersionFiles.get(relativePath);
      Path newVersionFilePath = newVersionFiles.get(relativePath);

      System.out.println("\n=== Comparando archivo: " + relativePath + " ===");

      if (oldVersionFilePath != null && newVersionFilePath != null) {
        List<String> oldVersionCodeLines = Files.readAllLines(oldVersionFilePath);
        List<String> newVersionCodeLines = Files.readAllLines(newVersionFilePath);

        compareFileVersions(oldVersionCodeLines, newVersionCodeLines);
      } else if (oldVersionFilePath != null) {
        System.out.println("Archivo eliminado en la nueva versión: " + relativePath);
      } else {
        System.out.println("Archivo nuevo en la nueva versión: " + relativePath);
      }
    }
  }

  private static void compareFileVersions(
      List<String> oldVersionCodeLines, List<String> newVersionCodeLines) {
    List<String> temporalCopyOfOldVersionCodeLines = new ArrayList<>(oldVersionCodeLines);
    List<String> temporalCopyOfNewVersionCodeLines = new ArrayList<>(newVersionCodeLines);

    List<String> unchangedLines = new ArrayList<>();
    List<String> addedLines = new ArrayList<>();
    List<String> deletedLines = new ArrayList<>();

    // Detectar líneas sin cambios y lineas añadidas
    for (String line : newVersionCodeLines) {
      if (temporalCopyOfOldVersionCodeLines.contains(line)) {
        temporalCopyOfOldVersionCodeLines.remove(line);
        unchangedLines.add(line);
      } else {
        addedLines.add(line);
      }
    }

    // Detectar líneas eliminadas
    for (String line : oldVersionCodeLines) {
      if (temporalCopyOfNewVersionCodeLines.contains(line)) {
        temporalCopyOfNewVersionCodeLines.remove(line); // Ya fue tratada como línea sin cambios
      } else {
        deletedLines.add(line);
      }
    }

    // Mostrar versión anterior
    System.out.println("=== VERSION ANTERIOR ===");
    List<String> temporalCopyOfDeletedLines = new ArrayList<>(deletedLines);

    for (String line : oldVersionCodeLines) {
      if (temporalCopyOfDeletedLines.contains(line)) {
        System.out.println(line + " // - [BORRADA]");
        temporalCopyOfDeletedLines.remove(line);
      } else {
        System.out.println(line);
      }
    }

    // Mostrar versión nueva
    System.out.println("\n=== VERSION ACTUAL ===");
    List<String> temporalCopyOfAddedLines = new ArrayList<>(addedLines);

    for (String lineInNewVersion : newVersionCodeLines) {
      if (temporalCopyOfAddedLines.contains(lineInNewVersion)) {
        boolean isModified = false;

        for (String lineInOldVersion : oldVersionCodeLines) {
          double levenshteinSimilarity =
              LevenshteinDistance.calculateSimilarity(lineInNewVersion, lineInOldVersion);

          if (levenshteinSimilarity >= 0.7) {
            isModified = true;
            break;
          }
        }

        if (isModified) {
          System.out.println(lineInNewVersion + " // ≈ [MODIFICADA]");
        } else {
          System.out.println(lineInNewVersion + " // + [NUEVA]");
        }

        temporalCopyOfAddedLines.remove(lineInNewVersion);
      } else {
        System.out.println(lineInNewVersion);
      }
    }

    System.out.println("\n--- RESUMEN ---");
    System.out.println("Líneas sin cambios: " + unchangedLines.size());
    System.out.println("Líneas añadidas: " + addedLines.size());
    System.out.println("Líneas eliminadas: " + deletedLines.size());
  }
}
