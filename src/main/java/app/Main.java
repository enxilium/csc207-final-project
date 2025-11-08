package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // The AppBuilder holds all the state and wiring logic.
        AppBuilder appBuilder = new AppBuilder();

        // Chain the building methods to construct the application piece by piece.
        JFrame application = appBuilder
                .addDemoView()
                .addWriteTestView()
                .addEvaluateTestView()
                .addLectureNotesView()
                .addLoadingView()
                .addMockTestGenerationUseCase()
                .addEvaluateTestUseCase()
                .addLectureNotesUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null); // Center the window
        application.setVisible(true);
    }
}