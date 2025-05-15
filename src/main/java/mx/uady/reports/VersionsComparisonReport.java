package mx.uady.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mx.uady.utils.LevenshteinDistance;

public class VersionsComparisonReport {
  public static void appendComparison(
      List<String> oldVersionCodeLines,
      List<String> newVersionCodeLines,
      List<String> unchangedLines,
      List<String> addedLines,
      List<String> deletedLines,
      StringBuilder reportBuilder) {
    reportBuilder.append("\n=== VERSION ANTERIOR ===\n");
    List<String> deletedCopy = new ArrayList<>(deletedLines);

    for (String line : oldVersionCodeLines) {
      if (deletedCopy.contains(line)) {
        reportBuilder.append(line).append(" // - [BORRADA]\n");
        deletedCopy.remove(line);
      } else {
        reportBuilder.append(line).append("\n");
      }
    }

    reportBuilder.append("\n=== VERSION ACTUAL ===\n");
    List<String> addedCopy = new ArrayList<>(addedLines);

    for (String newLine : newVersionCodeLines) {
      if (addedCopy.contains(newLine)) {
        boolean isModified = false;
        for (String oldLine : oldVersionCodeLines) {
          double similarity = LevenshteinDistance.calculateSimilarity(newLine, oldLine);
          if (similarity >= 0.7) {
            isModified = true;
            break;
          }
        }
        if (isModified) {
          reportBuilder.append(newLine).append(" // ≈ [MODIFICADA]\n");
        } else {
          reportBuilder.append(newLine).append(" // + [NUEVA]\n");
        }
        addedCopy.remove(newLine);
      } else {
        reportBuilder.append(newLine).append("\n");
      }
    }

    reportBuilder.append("\n--- RESUMEN ---\n");
    reportBuilder.append("Líneas sin cambios: ").append(unchangedLines.size()).append("\n");
    reportBuilder.append("Líneas añadidas: ").append(addedLines.size()).append("\n");
    reportBuilder.append("Líneas eliminadas: ").append(deletedLines.size()).append("\n\n");
  }

  public static void writeToFile(StringBuilder reportBuilder, String outputFilePath) {
    try {
      File outputFile = new File(outputFilePath);

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
        writer.write(reportBuilder.toString());
      }

      System.out.println("Reporte de cambios generado: " + outputFile.getAbsolutePath() + "\n");
    } catch (IOException e) {
      System.err.println("Error al escribir el reporte: " + e.getMessage() + "\n");
    }
  }
}
