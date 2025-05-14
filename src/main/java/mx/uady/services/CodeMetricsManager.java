package mx.uady.services;

import java.util.List;
import mx.uady.models.JavaClass;
import mx.uady.repositories.JavaClassRepository;

/*
 * Esta clase representa una fachada para gestionar un estado global
 * o acumulador de todas las métricas relacionadas con el número de
 * clases, métodos y líneas fisicas del código.
 */
public class CodeMetricsManager {
  private final JavaClassRepository classRepository;
  private final CodeStructuresMetricsService codeStructuresMetricsService;
  private final PhysicalLinesMetricsService physicalLinesMetricsService;

  public CodeMetricsManager() {
    this.classRepository = new JavaClassRepository();
    this.codeStructuresMetricsService = new CodeStructuresMetricsService(classRepository);
    this.physicalLinesMetricsService = new PhysicalLinesMetricsService(classRepository);
  }

  public List<JavaClass> getClasses() {
    return codeStructuresMetricsService.getAllClasses();
  }

  public void addClassIfNotExist(String className) {
    codeStructuresMetricsService.addClassIfNotExist(className);
  }

  public void incrementMethodCountInLastClass() {
    codeStructuresMetricsService.incrementMethodCountInLastClass();
  }

  public void increaseLineCountForClass(String className) {
    physicalLinesMetricsService.incrementLineCountForAClass(className);
  }

  public int getTotalLinesOfCode() {
    return physicalLinesMetricsService.calculateTotalLinesOfCode();
  }
}
