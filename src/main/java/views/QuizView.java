package views;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * View for displaying quiz content.
 * This view shows quiz information when a user clicks on a quiz item from the timeline.
 */
public class QuizView extends JPanel {
    private UUID contentId;

    public QuizView() {
        setLayout(new BorderLayout(8, 8));
        initializeUI();
    }

    private void initializeUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back to Timeline");
        JLabel titleLabel = new JLabel("Quiz", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("Select a quiz from the timeline to view quiz details.", SwingConstants.CENTER);
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 14f));
        contentPanel.add(infoLabel, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the view to display the specified quiz.
     * @param contentId The ID of the quiz content
     * @param numQuestions The number of questions in the quiz
     * @param score The score achieved (if submitted), or null if not submitted
     */
    public void displayQuiz(UUID contentId, int numQuestions, Double score) {
        this.contentId = contentId;

        // Update the UI
        JPanel contentPanel = (JPanel) getComponent(1);
        contentPanel.removeAll();
        
        String scoreText = score != null 
            ? String.format("Score: %.1f / %d (%.1f%%)", score, numQuestions, (score / numQuestions) * 100)
            : String.format("%d questions", numQuestions);
        
        JLabel infoLabel = new JLabel(
            String.format("<html><center>Quiz (ID: %s)<br>%s</center></html>", 
                contentId.toString().substring(0, 8), scoreText),
            SwingConstants.CENTER
        );
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 14f));
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        contentPanel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Gets the back button for adding action listeners.
     * @return The back button
     */
    public JButton getBackButton() {
        JPanel header = (JPanel) getComponent(0);
        return (JButton) header.getComponent(0);
    }

    public UUID getContentId() {
        return contentId;
    }
}


