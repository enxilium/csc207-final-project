package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();

        // ========== TIMELINE FEATURE START ==========
        // Add .addTimelineView() to your existing builder chain
        // Example: appBuilder.addCourseDashboard().addTimelineView().addMockTests()...
        // ========== TIMELINE FEATURE END ==========
        JFrame application = appBuilder
                .addTimelineView()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
