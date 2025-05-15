package mx.uady.usecases;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import mx.uady.reports.VersionsComparisonReport;
import mx.uady.utils.JavaFilesCollector;

public class ProjectVersionComparison {
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
    List<String> tempOld = new ArrayList<>(oldVersionCodeLines);
    List<String> tempNew = new ArrayList<>(newVersionCodeLines);

    List<String> unchangedLines = new ArrayList<>();
    List<String> addedLines = new ArrayList<>();
    List<String> deletedLines = new ArrayList<>();

    // Detectar líneas sin cambios y añadidas
    for (String line : newVersionCodeLines) {
      if (tempOld.contains(line)) {
        tempOld.remove(line);
        unchangedLines.add(line);
      } else {
        addedLines.add(line);
      }
    }

    // Detectar líneas eliminadas
    for (String line : oldVersionCodeLines) {
      if (tempNew.contains(line)) {
        tempNew.remove(line);
      } else {
        deletedLines.add(line);
      }
    }

    VersionsComparisonReport.printFilesComparison(
        oldVersionCodeLines, newVersionCodeLines, unchangedLines, addedLines, deletedLines);
  }
}
