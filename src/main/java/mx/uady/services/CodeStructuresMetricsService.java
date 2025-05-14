package mx.uady.services;

import java.util.List;
import java.util.Optional;
import mx.uady.models.JavaClass;
import mx.uady.repositories.JavaClassRepository;

public class CodeStructuresMetricsService {
  private final JavaClassRepository classRepository;

  public CodeStructuresMetricsService(JavaClassRepository classRepository) {
    this.classRepository = classRepository;
  }

  /**
   * Obtiene todas las clases registradas en el repositorio.
   *
   * @return Lista de clases registradas.
   */
  public List<JavaClass> getAllClasses() {
    return classRepository.getAllClasses();
  }

  /**
   * Registra una nueva clase si no existe en el repositorio.
   *
   * @param className Nombre de la clase a registrar.
   */
  public void addClassIfNotExist(String className) {
    classRepository.registerClassIfNotExist(className);
  }

  /**
   * Incrementa el contador de métodos para la última clase registrada.
   *
   * @return true si se logró incrementar, false si no hay clases registradas
   */
  public void incrementMethodCountInLastClass() {
    Optional<JavaClass> latestClass = classRepository.getLatestAddedClass();

    if (latestClass.isPresent()) {
      latestClass.get().incrementMethodsAmount();
    }
  }
}
