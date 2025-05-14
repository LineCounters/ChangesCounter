package mx.uady.reports;

import java.util.List;
import mx.uady.models.JavaClass;

public class ProgramMetricsReport {
  private String programName;
  private List<JavaClass> javaClasses;
  private int totalPhysicalLinesInProgram;

  private static final String LINE_SEPARATOR =
      "---------------------------------------------------------------------------------";

  public ProgramMetricsReport(
      String programName, List<JavaClass> javaClasses, int totalPhysicalLinesInProgram) {
    this.programName = programName;
    this.javaClasses = javaClasses;
    this.totalPhysicalLinesInProgram = totalPhysicalLinesInProgram;
  }

  public void printReport() {
    printReportHeader();
    printReportBody();
  }

  private void printReportHeader() {
    System.out.println(LINE_SEPARATOR);
    System.out.println("Programa: " + this.programName);
    System.out.println(LINE_SEPARATOR);
    System.out.printf(" %-40s  %-15s  %-15s %n", "Clases", "Métodos", "Lineas");
    System.out.println(LINE_SEPARATOR);
  }

  private void printReportBody() {
    javaClasses.forEach(
        javaClass ->
            System.out.printf(
                " %-40s  %-15d  %-15d %n",
                javaClass.getClassName(),
                javaClass.getMethodsAmount(),
                javaClass.getPhysicalLinesAmount()));

    System.out.println(LINE_SEPARATOR);

    System.out.println("Total de líneas físicas de código: " + totalPhysicalLinesInProgram);
  }
}
