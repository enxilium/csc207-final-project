package views;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * View for displaying flashcards content.
 * This view shows flashcards when a user clicks on a flashcards item from the timeline.
 */
public class FlashcardsView extends JPanel {
    private UUID contentId;

    public FlashcardsView() {
        setLayout(new BorderLayout(8, 8));
        initializeUI();
    }

    private void initializeUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back to Timeline");
        JLabel titleLabel = new JLabel("Flashcards", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("Select a flashcards set from the timeline to view flashcards.", SwingConstants.CENTER);
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 14f));
        contentPanel.add(infoLabel, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the view to display the specified flashcards.
     * @param contentId The ID of the flashcards content
     * @param numCards The number of flashcards in the set
     */
    public void displayFlashcards(UUID contentId, int numCards) {
        this.contentId = contentId;

        // Update the UI
        JPanel contentPanel = (JPanel) getComponent(1);
        contentPanel.removeAll();
        
        JLabel infoLabel = new JLabel(
            String.format("Flashcards Set (ID: %s)%n%d flashcards available", 
                contentId.toString().substring(0, 8), numCards),
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


