package mx.uady;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import mx.uady.models.JavaClass;
import mx.uady.reports.ProgramMetricsReport;
import mx.uady.services.CodeAnalyzer;
import mx.uady.services.CodeMetricsManager;
import mx.uady.utils.JavaFileSanitizer;
import mx.uady.utils.JavaFilesCollector;

public class Main {
  public static void main(String[] args) {
    Logger logger = Logger.getLogger(Main.class.getName());

    try (Scanner scanner = new Scanner(System.in)) {
      logger.info("Ingresa la ruta de la carpeta del proyecto: ");
      String pathToAnalize = scanner.nextLine();

      startProjectAnalysis(pathToAnalize);
    } catch (Exception exception) {
      logger.severe("Error: " + exception.getMessage());
    }
  }

  public static void startProjectAnalysis(String folderPath) throws Exception {
    try {
      CodeMetricsManager metricsManager = new CodeMetricsManager();

      List<Path> javaFilePaths = JavaFilesCollector.getJavaFilePathsByFolderPath(folderPath);

      for (Path javaFilePath : javaFilePaths) {
        CodeAnalyzer analyzer = new CodeAnalyzer(metricsManager);

        List<String> fileLines = Files.readAllLines(javaFilePath);
        List<String> fileLinesWithoutComments = JavaFileSanitizer.removeComments(fileLines);
        List<String> fileLinesWithoutBlankLines =
            JavaFileSanitizer.removeBlankLines(fileLinesWithoutComments);

        fileLinesWithoutBlankLines.forEach(analyzer::processLine);
      }

      generateReport(folderPath, metricsManager.getClasses(), metricsManager.getTotalLinesOfCode());
    } catch (Exception e) {
      throw e;
    }
  }

  private static void generateReport(
      String programName, List<JavaClass> javaClasses, int totalPhysicalLinesInProgram) {

    ProgramMetricsReport report =
        new ProgramMetricsReport(programName, javaClasses, totalPhysicalLinesInProgram);

    report.printReport();
  }
}
