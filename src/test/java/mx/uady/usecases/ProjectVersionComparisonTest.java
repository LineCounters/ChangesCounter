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
      Files.walk(testDir).map(Path::toFile).forEach(file -> file.delete());
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

    Files.write(oldVersion.resolve("Test.java"), List.of("linea1", "linea2", "linea3"));
    Files.write(newVersion.resolve("Test.java"), List.of("linea1", "linea3", "linea4"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("Líneas sin cambios: 1"));
    assertTrue(reportLines.contains("Líneas eliminadas: 2"));
    assertTrue(reportLines.contains("Líneas añadidas: 2"));
  }

  @Test
  @DisplayName("Debe detectar correctamente las modificaciones cuando una línea cambia de lugar")
  void testLineReordering()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(oldVersion.resolve("Test.java"), List.of("linea1", "linea2", "linea3"));
    Files.write(newVersion.resolve("Test.java"), List.of("linea3", "linea1", "linea2"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("linea3 // ≈ [MODIFICADA]"));
    assertTrue(reportLines.contains("linea1 // ≈ [MODIFICADA]"));
    assertTrue(reportLines.contains("linea2 // ≈ [MODIFICADA]"));
  }

  @Test
  @DisplayName("Debe detectar archivos nuevos y eliminados")
  void testNewAndDeletedFiles()
      throws IOException, FolderNotFoundException, JavaFilesNotFoundInPathException {
    Path oldVersion = testDir.resolve("oldVersion");
    Path newVersion = testDir.resolve("newVersion");
    Files.createDirectories(oldVersion);
    Files.createDirectories(newVersion);

    Files.write(oldVersion.resolve("OldFile.java"), List.of("linea1", "linea2"));
    Files.write(newVersion.resolve("NewFile.java"), List.of("linea3", "linea4"));

    ProjectVersionComparison.compareProjectVersions(oldVersion.toString(), newVersion.toString());

    List<String> reportLines = Files.readAllLines(Paths.get("versions_comparison_report.txt"));
    assertTrue(reportLines.contains("Archivo eliminado en la nueva versión: OldFile.java"));
    assertTrue(reportLines.contains("Archivo nuevo: NewFile.java"));
  }
}
