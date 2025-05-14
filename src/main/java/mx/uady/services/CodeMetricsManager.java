package mx.uady.services;

import java.util.List;
import mx.uady.models.JavaClass;
import mx.uady.repositories.JavaClassRepository;

/*
 * Esta clase representa una fachada para gestionar métricas de código,
 * como el conteo de clases, métodos y líneas físicas de un programa.
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

  public void queueClassIfNotExist(String className) {
    classRepository.registerClass(className);
  }

  public List<JavaClass> getClasses() {
    return classRepository.getAllClasses();
  }

  public void incrementMethodCountInLastClass() {
    codeStructuresMetricsService.incrementMethodCountForLastClass();
  }

  public void increaseLineCountForClass(String className) {
    physicalLinesMetricsService.incrementLineCountForAClass(className);
  }

  public int getTotalLinesOfCode() {
    return physicalLinesMetricsService.calculateTotalLinesOfCode();
  }
}
