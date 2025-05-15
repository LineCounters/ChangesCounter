package mx.uady.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VersionsComparisonReport {
  private static StringBuilder reportBuilder = new StringBuilder();

  public static void addLineToReport(String line) {
    reportBuilder.append(line).append(System.lineSeparator());
  }

  public static void writeReportToFile(String outputFilePath) {
    try {
      File outputFile = new File(outputFilePath);

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
        writer.write(reportBuilder.toString());
      }

      System.out.println("Reporte de cambios generado en: " + outputFile.getAbsolutePath());
      System.out.println();
    } catch (IOException e) {
      System.err.println("Error al escribir el reporte: " + e.getMessage());
    }
  }

  private VersionsComparisonReport() {}
}
