package mx.uady;

import java.util.Scanner;
import java.util.logging.Logger;
import mx.uady.usecases.CodeMetricsCollector;
import mx.uady.usecases.ProjectVersionComparision;

public class Main {
  public static void main(String[] args) {
    Logger logger = Logger.getLogger(Main.class.getName());

    try (Scanner scanner = new Scanner(System.in)) {
      logger.info("Ingresa la ruta de la carpeta de la versión anterior: ");
      String oldVersionPath = scanner.nextLine();

      logger.info("Ingresa la ruta de la carpeta de la nueva versión: ");
      String newVersionPath = scanner.nextLine();

      ProjectVersionComparision.compareProjectVersions(oldVersionPath, newVersionPath);

      CodeMetricsCollector.summarizeCodeMetrics(oldVersionPath);
      CodeMetricsCollector.summarizeCodeMetrics(newVersionPath);
    } catch (Exception exception) {
      logger.severe("Error: " + exception.getMessage());
    }
  }
}
