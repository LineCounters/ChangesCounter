package mx.uady.usecases;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import mx.uady.models.JavaClass;
import mx.uady.reports.ProgramMetricsReport;
import mx.uady.services.CodeAnalyzer;
import mx.uady.services.CodeMetricsManager;
import mx.uady.utils.JavaFileSanitizer;
import mx.uady.utils.JavaFilesCollector;

public class CodeMetricsCollector {
  public static void summarizeCodeMetrics(String folderPath) throws Exception {
    CodeMetricsManager metricsManager = new CodeMetricsManager();

    Map<String, Path> javaFilePaths = JavaFilesCollector.getJavaFilePathsByFolderPath(folderPath);

    for (Path javaFilePath : javaFilePaths.values()) {
      CodeAnalyzer analyzer = new CodeAnalyzer(metricsManager);

      List<String> fileLines = Files.readAllLines(javaFilePath);
      List<String> fileLinesWithoutComments = JavaFileSanitizer.removeComments(fileLines);
      List<String> fileLinesWithoutBlankLines =
          JavaFileSanitizer.removeBlankLines(fileLinesWithoutComments);

      fileLinesWithoutBlankLines.forEach(analyzer::processLine);
    }

    generateReport(folderPath, metricsManager.getClasses(), metricsManager.getTotalLinesOfCode());
  }

  private static void generateReport(
      String programName, List<JavaClass> javaClasses, int totalPhysicalLinesInProgram) {

    ProgramMetricsReport report =
        new ProgramMetricsReport(programName, javaClasses, totalPhysicalLinesInProgram);

    report.printReport();
  }
}
