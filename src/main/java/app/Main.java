package app;

import javax.swing.*;
import java.awt.*;
import views.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                .addCourseUseCases()
                .addMockTestGenerationUseCase()
                .addEvaluateTestUseCase()
                .addLectureNotesUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null); // Center the window
        application.setVisible(true);
    }
}