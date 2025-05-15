package mx.uady.utils;

public class LevenshteinDistance {

  /**
   * Calcula la distancia de Levenshtein entre 2 strings.
   *
   * @param source Primera string
   * @param target Segunda string
   * @return La distancia de Levenshtein
   */
  private static int calculateDistance(String source, String target) {
    if (source.isEmpty()) {
      return target.length();
    }

    if (target.isEmpty()) {
      return source.length();
    }

    int sourceLength = source.length();
    int targetLength = target.length();

    int[][] distanceMatrix = new int[sourceLength + 1][targetLength + 1];

    for (int i = 0; i <= sourceLength; i++) {
      distanceMatrix[i][0] = i;
    }

    for (int j = 0; j <= targetLength; j++) {
      distanceMatrix[0][j] = j;
    }

    for (int i = 1; i <= sourceLength; i++) {
      for (int j = 1; j <= targetLength; j++) {
        int cost = (source.charAt(i - 1) == target.charAt(j - 1)) ? 0 : 1;

        distanceMatrix[i][j] =
            Math.min(
                Math.min(
                    distanceMatrix[i - 1][j] + 1, // Deletion
                    distanceMatrix[i][j - 1] + 1), // Insertion
                distanceMatrix[i - 1][j - 1] + cost // Substitution
                );
      }
    }

    return distanceMatrix[sourceLength][targetLength];
  }

  /**
   * Calcula el radio de similaridad entre 2 strings. El radio es igual a 1 - (distance /
   * max(source.length, target.length))
   *
   * @param source Primera string
   * @param target Segunda string
   * @return El radio de similaridad en un valor entre 0 y 1
   */
  public static double calculateSimilarity(String source, String target) {
    int distance = calculateDistance(source, target);
    int maxLength = Math.max(source.length(), target.length());

    if (maxLength == 0) {
      return 1.0; // Ambas string están vacías
    }

    return 1.0 - ((double) distance / maxLength);
  }
}
