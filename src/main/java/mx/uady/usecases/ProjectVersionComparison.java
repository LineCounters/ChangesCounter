package mx.uady.usecases;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.uady.reports.VersionsComparisonReport;
import mx.uady.utils.JavaFilesCollector;

public class ProjectVersionComparison {
  private static final StringBuilder reportBuilder = new StringBuilder();

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

      reportBuilder.append("\n--- Comparando archivo: " + relativePath + " ---");

      if (oldVersionFilePath != null && newVersionFilePath != null) {
        List<String> oldVersionCodeLines = Files.readAllLines(oldVersionFilePath);
        List<String> newVersionCodeLines = Files.readAllLines(newVersionFilePath);

        compareFileVersions(oldVersionCodeLines, newVersionCodeLines);
      } else if (oldVersionFilePath != null) {
        reportBuilder.append("\nArchivo eliminado en la nueva versión: " + relativePath);
      } else {
        reportBuilder.append("\nArchivo nuevo en la nueva versión: " + relativePath);
      }
    }

    VersionsComparisonReport.writeToFile(reportBuilder, "versions_comparison_report.txt");
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

    VersionsComparisonReport.appendComparison(
        oldVersionCodeLines,
        newVersionCodeLines,
        unchangedLines,
        addedLines,
        deletedLines,
        reportBuilder);
  }
}
