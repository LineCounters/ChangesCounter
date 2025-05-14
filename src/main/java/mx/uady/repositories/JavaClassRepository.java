package mx.uady.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mx.uady.models.JavaClass;

/**
 * Repositorio que gestiona la colección de clases Java a analizar y proporciona métodos para su
 * búsqueda y registro.
 */
public class JavaClassRepository {
  private final List<JavaClass> javaClasses;

  public JavaClassRepository() {
    this.javaClasses = new ArrayList<>();
  }

  /**
   * Busca una clase por su nombre.
   *
   * @param className Nombre de la clase a buscar
   * @return Optional con la clase si existe, o vacío si no
   */
  public Optional<JavaClass> findClassByName(String className) {
    if (className == null) {
      return Optional.empty();
    }

    return javaClasses.stream()
        .filter(javaClass -> className.equals(javaClass.getClassName()))
        .findFirst();
  }

  /**
   * Obtiene la última clase añadida al repositorio.
   *
   * @return Optional con la última clase registrada, o vacío si no hay clases
   */
  public Optional<JavaClass> getLatestAddedClass() {
    if (javaClasses.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(javaClasses.get(javaClasses.size() - 1));
  }

  /**
   * Obtiene una copia no modificable de la lista de clases registradas.
   *
   * @return Lista de clases Java
   */
  public List<JavaClass> getAllClasses() {
    return Collections.unmodifiableList(javaClasses);
  }

  /**
   * Registra una nueva clase Java si no existe previamente.
   *
   * @param className Nombre de la clase a registrar
   * @return La instancia de JavaClass existente o la recién creada
   * @throws IllegalArgumentException si el nombre de la clase es nulo
   */
  public JavaClass registerClassIfNotExist(String className) {
    Objects.requireNonNull(className, "El nombre de la clase no puede ser nulo");

    return findClassByName(className)
        .orElseGet(
            () -> {
              JavaClass newClass = new JavaClass(className);
              javaClasses.add(newClass);
              return newClass;
            });
  }
}
