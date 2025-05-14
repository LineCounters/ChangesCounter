package mx.uady.models;

public class JavaClass {
  private String className;
  private int methodsAmount;
  private int physicalLinesAmount;

  public JavaClass(String className) {
    if (className == null || className.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de la clase no puede ser nulo o vacío.");
    }

    this.className = className;
    this.methodsAmount = 0;
    this.physicalLinesAmount = 0;
  }

  public String getClassName() {
    return className;
  }

  public int getMethodsAmount() {
    return methodsAmount;
  }

  public void incrementMethodsAmount() {
    this.methodsAmount++;
  }

  public int getPhysicalLinesAmount() {
    return physicalLinesAmount;
  }

  public void incrementPhysicalLinesAmount() {
    this.physicalLinesAmount++;
  }
}
