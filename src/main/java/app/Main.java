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
                .addWriteTestView()
                .addEvaluateTestView()
                .addLoadingView()
                .addCourseDashboardView()
                .addCourseWorkspaceView()
                .addCourseCreateView()
                .addCourseEditView()
                .addCourseUseCases()
                .addMockTestGenerationUseCase()
                .addEvaluateTestUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null); // Center the window
        application.setVisible(true);
    }
}