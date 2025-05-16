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
  private static final double SIMILARITY_THRESHOLD = 0.7;
  private static final int MAX_LINE_LENGTH = 80;

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
    List<Integer> unchangedOldLines = new ArrayList<>();
    List<Integer> unchangedNewLines = new ArrayList<>();

    int minLength = Math.min(oldVersionCodeLines.size(), newVersionCodeLines.size());
    for (int i = 0; i < minLength; i++) {
      if (oldVersionCodeLines.get(i).equals(newVersionCodeLines.get(i))) {
        unchangedOldLines.add(i);
        unchangedNewLines.add(i);
      }
    }

    List<LineMapping> modifiedLines = new ArrayList<>();

    for (int oldIndex = 0; oldIndex < oldVersionCodeLines.size(); oldIndex++) {
      if (unchangedOldLines.contains(oldIndex)) continue;

      String oldLine = oldVersionCodeLines.get(oldIndex);

      for (int newIndex = 0; newIndex < newVersionCodeLines.size(); newIndex++) {
        if (unchangedNewLines.contains(newIndex)) continue;

        String newLine = newVersionCodeLines.get(newIndex);
        double similarity = LevenshteinDistance.calculateSimilarity(oldLine, newLine);

        if (similarity >= SIMILARITY_THRESHOLD && oldIndex == newIndex) {
          modifiedLines.add(new LineMapping(oldIndex, newIndex, similarity));
          break;
        }
      }
    }

    addOldFileVersionToReport(oldVersionCodeLines, unchangedOldLines, modifiedLines);
    addNewFileVersionToReport(newVersionCodeLines, unchangedNewLines, modifiedLines);

    int unchangedCount = unchangedOldLines.size();
    int deletedCount = oldVersionCodeLines.size() - unchangedOldLines.size() - modifiedLines.size();
    int addedCount = newVersionCodeLines.size() - unchangedNewLines.size() - modifiedLines.size();
    int modifiedCount = modifiedLines.size();

    addSummaryToReport(unchangedCount, addedCount, deletedCount, modifiedCount);
  }

  private static void addOldFileVersionToReport(
      List<String> oldVersionCodeLines,
      List<Integer> unchangedLines,
      List<LineMapping> modifiedLines) {
    VersionsComparisonReport.addLineToReport("=== VERSION ANTERIOR ===");

    for (int i = 0; i < oldVersionCodeLines.size(); i++) {
      String line = oldVersionCodeLines.get(i);

      if (unchangedLines.contains(i)) {
        VersionsComparisonReport.addLineToReport(formatLineForReport(line));
      } else {
        boolean isModified = false;

        for (LineMapping mapping : modifiedLines) {
          if (mapping.oldIndex == i) {
            VersionsComparisonReport.addLineToReport(
                formatLineForReport(line + " // ≈ [MODIFICADA]"));
            isModified = true;
            break;
          }
        }

        if (!isModified) {
          VersionsComparisonReport.addLineToReport(formatLineForReport(line + " // - [BORRADA]"));
        }
      }
    }
  }

  private static void addNewFileVersionToReport(
      List<String> newVersionCodeLines,
      List<Integer> unchangedLines,
      List<LineMapping> modifiedLines) {
    VersionsComparisonReport.addLineToReport("=== VERSION ACTUAL ===");

    for (int i = 0; i < newVersionCodeLines.size(); i++) {
      String line = newVersionCodeLines.get(i);

      if (unchangedLines.contains(i)) {
        VersionsComparisonReport.addLineToReport(formatLineForReport(line));
      } else {
        boolean isModified = false;

        for (LineMapping mapping : modifiedLines) {
          if (mapping.newIndex == i) {
            VersionsComparisonReport.addLineToReport(
                formatLineForReport(line + " // ≈ [MODIFICADA]"));
            isModified = true;
            break;
          }
        }

        if (!isModified) {
          VersionsComparisonReport.addLineToReport(formatLineForReport(line + " // + [NUEVA]"));
        }
      }
    }
  }

  private static void addSummaryToReport(
      int unchangedLinesCount, int addedLinesCount, int deletedLinesCount, int modifiedLinesCount) {
    VersionsComparisonReport.addLineToReport("--- RESUMEN ---");
    VersionsComparisonReport.addLineToReport("Líneas sin cambios: " + unchangedLinesCount);
    VersionsComparisonReport.addLineToReport("Líneas añadidas: " + addedLinesCount);
    VersionsComparisonReport.addLineToReport("Líneas eliminadas: " + deletedLinesCount);
    VersionsComparisonReport.addLineToReport("Líneas modificadas: " + modifiedLinesCount);
    VersionsComparisonReport.addLineToReport("- - - - - - - -");
  }

  private static String formatLineForReport(String line) {
    if (line.length() <= MAX_LINE_LENGTH) return line;

    StringBuilder formatted = new StringBuilder();
    int index = 0;
    while (index < line.length()) {
      int end = Math.min(index + MAX_LINE_LENGTH, line.length());
      formatted.append(line, index, end).append(System.lineSeparator());
      index = end;
    }
    return formatted.toString().trim();
  }

  private static class LineMapping {
    final int oldIndex;
    final int newIndex;
    final double similarity;

    LineMapping(int oldIndex, int newIndex, double similarity) {
      this.oldIndex = oldIndex;
      this.newIndex = newIndex;
      this.similarity = similarity;
    }
  }

  private ProjectVersionComparison() {}
}
