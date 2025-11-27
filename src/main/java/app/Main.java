package app;

import javax.swing.*;
import java.awt.*;
import views.*;

public class Main {
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