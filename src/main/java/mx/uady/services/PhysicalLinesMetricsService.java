package mx.uady.services;

import java.util.Objects;
import mx.uady.models.JavaClass;
import mx.uady.repositories.JavaClassRepository;

public class PhysicalLinesMetricsService {
  private final JavaClassRepository classRepository;

  public PhysicalLinesMetricsService(JavaClassRepository classRepository) {
    this.classRepository = classRepository;
  }

  /**
   * Incrementa el contador de líneas de código para una clase específica.
   *
   * @param className Nombre de la clase
   * @throws IllegalArgumentException si el nombre de la clase es nulo
   * @throws IllegalStateException si la clase no está registrada
   */
  public void incrementLineCountForAClass(String className) {
    Objects.requireNonNull(className, "El nombre de la clase no puede ser nulo");

    JavaClass targetClass =
        classRepository
            .findClassByName(className)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Clase no encontrada al intentar añadir línea: " + className));

    targetClass.incrementPhysicalLinesAmount();
  }

  /**
   * Calcula el total de líneas de código de todas las clases en el repositorio.
   *
   * @return Número total de líneas de código
   */
  public int calculateTotalLinesOfCode() {
    return classRepository.getAllClasses().stream()
        .mapToInt(JavaClass::getPhysicalLinesAmount)
        .sum();
  }
}
