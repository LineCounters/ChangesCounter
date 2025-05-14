package mx.uady.services;

import java.util.ArrayDeque;
import java.util.Deque;
import mx.uady.recognizers.CodeStructureRecognizer;
import mx.uady.recognizers.CodeStructureRecognizerFactory;
import mx.uady.recognizers.MethodDeclarationRecognizer;
import mx.uady.recognizers.TypeDeclarationRecognizer;

/** Analiza líneas de código Java para contar las clases, los métodos y las líneas físicas. */
public class CodeAnalyzer {
  private final CodeMetricsManager metricsManager;

  private final Deque<String> classContextStack;
  private final Deque<Integer> classStartBraceLevelStack;

  private int currentBraceLevel = 0;

  /**
   * Constructor.
   *
   * @param codeMetricsManager El manejador de las metricas donde se irán almacenando los
   *     resultados.
   */
  public CodeAnalyzer(CodeMetricsManager codeMetricsManager) {
    this.metricsManager = codeMetricsManager;
    this.classContextStack = new ArrayDeque<>();
    this.classStartBraceLevelStack = new ArrayDeque<>();
  }

  /**
   * Procesa una línea de código, detectando declaraciones de clases y métodos, y actualizando el
   * contexto de clases.
   *
   * @param line La línea de código a procesar.
   */
  public void processLine(String line) {
    String trimmedLine = preprocessLine(line);

    countLineForCurrentContext();

    DeclarationDetails declarationInformation = detectDeclarations(trimmedLine);

    processBracesAndUpdateContext(line, declarationInformation.potentialClassName());

    if (declarationInformation.isMethod()) {
      incrementMethodCount();
    }
  }

  /**
   * Preprocesa la línea: recorta espacios y verifica si debe ignorarse.
   *
   * @param line La línea original.
   * @return La línea recortada, o null si debe ignorarse.
   */
  public String preprocessLine(String line) {
    String trimmedLine = line.trim();

    if (trimmedLine.isEmpty()) {
      return null;
    }

    return trimmedLine;
  }

  /** Añade una línea a todas las clases en el contexto actual. */
  private void countLineForCurrentContext() {
    if (!classContextStack.isEmpty()) {
      for (String classNameInStack : classContextStack) {
        metricsManager.increaseLineCountForClass(classNameInStack);
      }
    }
  }

  /**
   * Determina si la línea contiene una declaración de tipo o método.
   *
   * @param trimmedLine La línea de código recortada.
   * @return Un objeto DeclarationInfo con los resultados.
   */
  private DeclarationDetails detectDeclarations(String trimmedLine) {
    CodeStructureRecognizer validator =
        CodeStructureRecognizerFactory.getRecognizerForCodeLine(trimmedLine);
    boolean isType = validator instanceof TypeDeclarationRecognizer;
    boolean isMethod = validator instanceof MethodDeclarationRecognizer;
    String className = null;

    if (isType) {
      className = ((TypeDeclarationRecognizer) validator).getTypeIdentifier(trimmedLine);

      if (className == null || className.trim().isEmpty()) {
        isType = false;
        className = null;
      }
    }

    return new DeclarationDetails(isType, isMethod, className);
  }

  /**
   * Itera sobre los caracteres de la línea, actualiza el nivel de llaves, y llama a push/pop del
   * contexto de clase cuando corresponde.
   *
   * @param line La línea de código original (para iterar caracteres).
   * @param potentialClassNameForLine El nombre de la clase declarada en ESTA línea (si aplica),
   *     null si no.
   */
  private void processBracesAndUpdateContext(String line, String potentialClassNameForLine) {
    int levelBeforeProcessingLine = currentBraceLevel;
    boolean classAddedOnThisLine = false;

    for (char c : line.toCharArray()) {
      if (c == '{') {
        if (potentialClassNameForLine != null && !classAddedOnThisLine) {
          pushClassContext(potentialClassNameForLine, levelBeforeProcessingLine);
          classAddedOnThisLine = true;
        }
        currentBraceLevel++;
      } else if (c == '}') {
        tryPopClassContext();
        if (currentBraceLevel > 0) {
          currentBraceLevel--;
        }
      }
    }

    if (potentialClassNameForLine != null && !classAddedOnThisLine) {
      pushClassContext(potentialClassNameForLine, currentBraceLevel);
    }
  }

  /**
   * Intenta sacar una clase del contexto si el nivel de llaves actual coincide con el cierre
   * esperado de la clase más interna.
   */
  private void tryPopClassContext() {
    if (!classContextStack.isEmpty()
        && !classStartBraceLevelStack.isEmpty()
        && currentBraceLevel == classStartBraceLevelStack.peek() + 1) {
      popClassContext();
    }
  }

  /**
   * Añade una clase al contexto y actualiza los contadores.
   *
   * @param className Nombre de la clase.
   * @param braceLevel Nivel de llaves antes de la apertura de esta clase.
   */
  private void pushClassContext(String className, int braceLevel) {
    if (classContextStack.isEmpty() || !classContextStack.peek().equals(className)) {
      classContextStack.push(className);
      classStartBraceLevelStack.push(braceLevel);

      metricsManager.addClassIfNotExist(className);
      metricsManager.increaseLineCountForClass(className);
    }
  }

  /** Saca la clase más interna del contexto (de ambas pilas). */
  private void popClassContext() {
    if (!classContextStack.isEmpty()) {
      classContextStack.pop();
    }

    if (!classStartBraceLevelStack.isEmpty()) {
      classStartBraceLevelStack.pop();
    }
  }

  /** Incrementa el número de métodos en la última clase. */
  private void incrementMethodCount() {
    if (!classContextStack.isEmpty()) {
      metricsManager.incrementMethodCountInLastClass();
    }
  }
}

record DeclarationDetails(boolean isType, boolean isMethod, String potentialClassName) {}
