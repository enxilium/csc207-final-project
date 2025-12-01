package app;

import javax.swing.JFrame;

import app.AppBuilder;

/**
 * Main entry point for the StudyFlow AI Assistant application.
 */
public class Main {
  /**
   * Main method that initializes and launches the application.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    // The AppBuilder holds all the state and wiring logic.
    AppBuilder appBuilder = new AppBuilder();

    // Chain the building methods to construct the application piece by piece.
    JFrame application = appBuilder
        .addLoadingView()
        .addWriteTestView()
        .addEvaluateTestView()
        .addLectureNotesView()
        .addCourseDashboardView()
        .addCourseWorkspaceView()
        .addCourseCreateView()
        .addCourseEditView()
        .addFileManagementView()
        // === Flashcard Views ===
        .addFlashcardViews()  // Add flashcard UI

        // === Use Cases ===
        .addMockTestGenerationUseCase()
        .addEvaluateTestUseCase()
        .addFlashcardGenerationUseCase()
        .addLectureNotesUseCase()
        .addCourseUseCases()
        .build();

    application.pack();
    application.setLocationRelativeTo(null); // Center the window
    application.setVisible(true);
  }
}
