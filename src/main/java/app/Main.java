package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel for better UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If setting look and feel fails, continue with default
        }

        // Build the application using the AppBuilder
        JFrame application = new AppBuilder()
                // === Core Views ===
                .addWriteTestView()
                .addEvaluateTestView()
                .addLoadingView()

                // === Course Management Views ===
                .addCourseDashboardView()
                .addCourseWorkspaceView()
                .addCourseCreateView()
                .addCourseEditView()

                // === Flashcard Views ===
                .addFlashcardViews()  // Add flashcard UI

                // === Use Cases ===
                .addMockTestGenerationUseCase()
                .addEvaluateTestUseCase()
                .addCourseUseCases()

                // === Flashcard Use Case ===
                .addFlashcardGenerationUseCase()  // Wire up flashcard logic

                // === Build ===
                .build();

        // Display the application
        application.pack();
        application.setVisible(true);
    }
}