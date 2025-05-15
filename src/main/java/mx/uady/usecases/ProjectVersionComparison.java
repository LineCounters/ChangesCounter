package mx.uady.usecases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.uady.exceptions.FolderNotFoundException;
import mx.uady.exceptions.JavaFilesNotFoundInPathException;
import mx.uady.reports.VersionsComparisonReport;
import mx.uady.utils.JavaFilesCollector;
import mx.uady.utils.LevenshteinDistance;

public class ProjectVersionComparison {
  public static void compareProjectVersions(
      String oldVersionFolderPath, String newVersionFolderPath)
      throws FolderNotFoundException, JavaFilesNotFoundInPathException, IOException {
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

      VersionsComparisonReport.addLineToReport(
          "=== Comparando archivo: " + relativePath + " ===" + System.lineSeparator());

      if (oldVersionFilePath != null && newVersionFilePath != null) {
        List<String> oldVersionCodeLines = Files.readAllLines(oldVersionFilePath);
        List<String> newVersionCodeLines = Files.readAllLines(newVersionFilePath);

        compareFileVersions(oldVersionCodeLines, newVersionCodeLines);
      } else if (oldVersionFilePath != null) {
        VersionsComparisonReport.addLineToReport(
            "Archivo eliminado en la nueva versión: " + relativePath);
      } else {
        VersionsComparisonReport.addLineToReport("Archivo nuevo: " + relativePath);
      }
    }

    VersionsComparisonReport.writeReportToFile("versions_comparison_report.txt");
  }

  private static void compareFileVersions(
      List<String> oldVersionCodeLines, List<String> newVersionCodeLines) {

    List<String> deletedLines = new ArrayList<>();
    List<String> addedLines = new ArrayList<>();
    List<String> unchangedLines = new ArrayList<>();

    Set<String> oldLinesSet = new HashSet<>(oldVersionCodeLines);
    Set<String> newLinesSet = new HashSet<>(newVersionCodeLines);

    int minSize = Math.min(oldVersionCodeLines.size(), newVersionCodeLines.size());
    for (int i = 0; i < minSize; i++) {
      if (oldVersionCodeLines.get(i).equals(newVersionCodeLines.get(i))) {
        unchangedLines.add(oldVersionCodeLines.get(i));
      }
    }

    for (int i = 0; i < oldVersionCodeLines.size(); i++) {
      String line = oldVersionCodeLines.get(i);
      if (newLinesSet.contains(line)
          && (i >= newVersionCodeLines.size() || !line.equals(newVersionCodeLines.get(i)))) {
        deletedLines.add(line);
      } else if (!newLinesSet.contains(line)) {
        deletedLines.add(line);
      }
    }

    for (int i = 0; i < newVersionCodeLines.size(); i++) {
      String line = newVersionCodeLines.get(i);
      if (oldLinesSet.contains(line)
          && (i >= oldVersionCodeLines.size() || !line.equals(oldVersionCodeLines.get(i)))) {
        addedLines.add(line);
      } else if (!oldLinesSet.contains(line)) {
        addedLines.add(line);
      }
    }

    addOldFileVersionToReport(oldVersionCodeLines, deletedLines);
    addNewFileVersionToReport(oldVersionCodeLines, newVersionCodeLines, addedLines);
    addSummaryToReport(unchangedLines.size(), addedLines.size(), deletedLines.size());
  }

  private static void addOldFileVersionToReport(
      List<String> oldVersionCodeLines, List<String> deletedLines) {
    VersionsComparisonReport.addLineToReport("=== VERSION ANTERIOR ===");
    List<String> temporalCopyOfDeletedLines = new ArrayList<>(deletedLines);

    for (String lineInOldVersion : oldVersionCodeLines) {
      if (temporalCopyOfDeletedLines.contains(lineInOldVersion)) {
        VersionsComparisonReport.addLineToReport(lineInOldVersion + " // - [ELIMINADA]");
        temporalCopyOfDeletedLines.remove(lineInOldVersion);
      } else {
        VersionsComparisonReport.addLineToReport(lineInOldVersion);
      }
    }
  }

  private static void addNewFileVersionToReport(
      List<String> oldVersionCodeLines, List<String> newVersionCodeLines, List<String> addedLines) {
    VersionsComparisonReport.addLineToReport("=== VERSION ACTUAL ===");
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
          VersionsComparisonReport.addLineToReport(lineInNewVersion + " // ≈ [MODIFICADA]");
        } else {
          VersionsComparisonReport.addLineToReport(lineInNewVersion + " // + [NUEVA]");
        }

        temporalCopyOfAddedLines.remove(lineInNewVersion);
      } else {
        VersionsComparisonReport.addLineToReport(lineInNewVersion);
      }
    }
  }

  private static void addSummaryToReport(
      int unchangedLinesCount, int addedLinesCount, int deletedLinesCount) {
    VersionsComparisonReport.addLineToReport("--- RESUMEN ---");
    VersionsComparisonReport.addLineToReport("Líneas sin cambios: " + unchangedLinesCount);
    VersionsComparisonReport.addLineToReport("Líneas añadidas: " + addedLinesCount);
    VersionsComparisonReport.addLineToReport("Líneas eliminadas: " + deletedLinesCount);
    VersionsComparisonReport.addLineToReport("- - - - - - - -");
  }

  private ProjectVersionComparison() {}
}
