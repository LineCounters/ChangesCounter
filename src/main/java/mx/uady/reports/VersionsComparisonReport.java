package mx.uady.reports;

import java.util.ArrayList;
import java.util.List;
import mx.uady.utils.LevenshteinDistance;

public class VersionsComparisonReport {
  public static void printFilesComparison(
      List<String> oldVersionCodeLines,
      List<String> newVersionCodeLines,
      List<String> unchangedLines,
      List<String> addedLines,
      List<String> deletedLines) {

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
