package mx.uady.services;

import mx.uady.repositories.JavaClassRepository;

public class CodeStructuresMetricsService {
  private final JavaClassRepository classRepository;

  public CodeStructuresMetricsService(JavaClassRepository classRepository) {
    this.classRepository = classRepository;
  }

  /**
   * Incrementa el contador de métodos para la última clase registrada.
   *
   * @return true si se logró incrementar, false si no hay clases registradas
   */
  public boolean incrementMethodCountForLastClass() {
    return classRepository
        .getLatestAddedClass()
        .map(
            javaClass -> {
              javaClass.incrementMethodsAmount();
              return true;
            })
        .orElse(false);
  }
}
