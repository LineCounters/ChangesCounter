package mx.uady.usecases;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import mx.uady.exceptions.FolderNotFoundException;
import mx.uady.exceptions.JavaFilesNotFoundInPathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectVersionComparisonTest {

  private final Path testDir = Paths.get("test_versions");

  @AfterEach
  void cleanUp() throws IOException {
    if (Files.exists(testDir)) {
      Files.walk(testDir).map(Path::toFile).forEach(java.io.File::delete);
    }
    Files.deleteIfExists(Paths.get("versions_comparison_report.txt"));
  }

  @Test
  @DisplayName("Debe contar correctamente las líneas añadidas, eliminadas y sin cambios")
  void testLineCounting()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(
        oldVersion.resolve("Test.java"),
        List.of("public class Test {", "  public void methodA() {", "    int a = 1;", "  }", "}"));
    Files.write(
        newVersion.resolve("Test.java"),
        List.of(
            "public class Test {",
            "  public void methodA() {",
            "    int b = 2;",
            "  }",
            "  public void methodB() {}",
            "}"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("Líneas sin cambios: 3"));
    assertTrue(reportLines.contains("Líneas añadidas: 3"));
    assertTrue(reportLines.contains("Líneas eliminadas: 2"));
  }

  @Test
  @DisplayName("Debe detectar correctamente las modificaciones cuando una línea cambia de lugar")
  void testLineReordering()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(
        oldVersion.resolve("Test.java"),
        List.of(
            "public class Test {",
            "  public void methodA() {}",
            "  public void methodB() {}",
            "  public void methodC() {}",
            "}"));
    Files.write(
        newVersion.resolve("Test.java"),
        List.of(
            "public class Test {",
            "  public void methodC() {}",
            "  public void methodA() {}",
            "  public void methodB() {}",
            "}"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("  public void methodC() {} // ≈ [MODIFICADA]"));
    assertTrue(reportLines.contains("  public void methodA() {} // ≈ [MODIFICADA]"));
    assertTrue(reportLines.contains("  public void methodB() {} // ≈ [MODIFICADA]"));
  }

  @Test
  @DisplayName("Debe detectar archivos nuevos y eliminados")
  void testNewAndDeletedFiles()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(
        oldVersion.resolve("OldFile.java"),
        List.of("public class OldFile {", "  public void oldMethod() {}", "}"));
    Files.write(
        newVersion.resolve("NewFile.java"),
        List.of("public class NewFile {", "  public void newMethod() {}", "}"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("Archivo eliminado en la nueva versión: OldFile.java"));
    assertTrue(reportLines.contains("Archivo nuevo: NewFile.java"));
  }

  @Test
  @DisplayName(
      "Debe marcar líneas largas con el sufijo correcto y respetar el límite de 80 caracteres")
  void testLongLinesAreWrappedAndSuffixed()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    // Línea de 160 caracteres (más de 80)
    String longLineOld = "a".repeat(160);
    String longLineNew = "a".repeat(159) + "b"; // Cambia el último carácter

    Files.write(oldVersion.resolve("LongLine.java"), List.of(longLineOld));
    Files.write(newVersion.resolve("LongLine.java"), List.of(longLineNew));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));

    long countWrapped =
        reportLines.stream()
            .filter(line -> line.equals("a".repeat(80)) || line.equals("a".repeat(80)))
            .count();
    assertTrue(countWrapped >= 2);

    assertTrue(reportLines.stream().anyMatch(line -> line.endsWith("// ≈ [MODIFICADA]")));

    for (String line : reportLines) {
      if (line.contains("//")) {
        String content = line.substring(0, line.indexOf("//")).trim();
        assertTrue(content.length() <= 80);
      }
    }
  }

  @Test
  @DisplayName("Debe envolver correctamente líneas nuevas largas y marcar con el sufijo de nueva")
  void testLongNewLinesAreWrappedAndSuffixed()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(oldVersion.resolve("Test.java"), List.of("short line"));
    String longNewLine = "b".repeat(100);
    Files.write(newVersion.resolve("Test.java"), List.of("short line", longNewLine));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));

    long countWrapped =
        reportLines.stream()
            .filter(line -> line.equals("b".repeat(80)) || line.startsWith("b".repeat(20)))
            .count();
    assertTrue(countWrapped >= 2);

    assertTrue(reportLines.stream().anyMatch(line -> line.endsWith("// + [NUEVA]")));

    for (String line : reportLines) {
      if (line.contains("//")) {
        String content = line.substring(0, line.indexOf("//")).trim();
        assertTrue(content.length() <= 80);
      }
    }
  }
}
