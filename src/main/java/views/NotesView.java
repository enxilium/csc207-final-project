package views;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * View for displaying notes content.
 * This view shows the notes title and content when a user clicks on a notes item from the timeline.
 */
public class NotesView extends JPanel {
    private UUID contentId;

    public NotesView() {
        setLayout(new BorderLayout(8, 8));
        initializeUI();
    }

    private void initializeUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back to Timeline");
        JLabel titleLabel = new JLabel("Notes", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Content area
        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setText("Select a notes item from the timeline to view its content.");

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the view to display the specified notes content.
     * @param contentId The ID of the notes content
     * @param title The title of the notes
     * @param content The content/snippet of the notes
     */
    public void displayNotes(UUID contentId, String title, String content) {
        this.contentId = contentId;

        // Update the UI
        JPanel header = (JPanel) getComponent(0);
        JLabel titleLabel = (JLabel) header.getComponent(1);
        titleLabel.setText(title != null && !title.isEmpty() ? title : "Notes");

        JScrollPane scrollPane = (JScrollPane) getComponent(1);
        JTextArea contentArea = (JTextArea) scrollPane.getViewport().getView();
        contentArea.setText(content != null ? content : "No content available.");
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

