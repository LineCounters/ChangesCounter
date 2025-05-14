package mx.uady.models;

public class JavaClass {
  private String className;
  private int methodsCount;
  private int physicalLinesCount;

  public JavaClass(String className) {
    if (className == null || className.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de la clase no puede ser nulo o vacío.");
    }

    this.className = className;
    this.methodsCount = 0;
    this.physicalLinesCount = 0;
  }

  public String getClassName() {
    return className;
  }

  public int getMethodsCount() {
    return methodsCount;
  }

  public void incrementMethodsAmount() {
    this.methodsCount++;
  }

  public int getPhysicalLinesCount() {
    return physicalLinesCount;
  }

  public void incrementPhysicalLinesCount() {
    this.physicalLinesCount++;
  }
}
