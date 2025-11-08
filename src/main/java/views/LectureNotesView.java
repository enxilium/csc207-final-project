package views;

import interface_adapters.lecturenotes.GenerateLectureNotesController;
import interface_adapters.lecturenotes.LectureNotesState;
import interface_adapters.lecturenotes.LectureNotesViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Swing View for the Lecture Notes use case.
 * - Shows a text field for topic
 * - Has a "Generate Notes" button
 * - Displays generated notes in a large text area
 * - Shows error messages if something goes wrong
 */
public class LectureNotesView extends JPanel implements PropertyChangeListener {

    private final LectureNotesViewModel viewModel;
    private final GenerateLectureNotesController controller;

    // UI components
    private final JTextField topicField;
    private final JTextArea notesArea;
    private final JLabel errorLabel;
    private final JButton generateButton;

    // For now we hard-code the course ID used by HardCodedCourseLookup.
    // Later, when Course Management is done, this can be injected.
    private final String courseId = "CSC207";

    public LectureNotesView(LectureNotesViewModel viewModel,
                            GenerateLectureNotesController controller) {
        this.viewModel = viewModel;
        this.controller = controller;

        // Listen for changes to the ViewModel
        this.viewModel.addPropertyChangeListener(this);

        // ---------- Build UI ----------
        setLayout(new BorderLayout());

        // Top panel: topic input + button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(8, 8));

        JLabel topicLabel = new JLabel("Topic:");
        topicField = new JTextField();
        generateButton = new JButton("Generate Notes");

        JPanel topicInputPanel = new JPanel(new BorderLayout(4, 4));
        topicInputPanel.add(topicLabel, BorderLayout.WEST);
        topicInputPanel.add(topicField, BorderLayout.CENTER);

        topPanel.add(topicInputPanel, BorderLayout.CENTER);
        topPanel.add(generateButton, BorderLayout.EAST);

        // Center: notes area
        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(notesArea);

        // Bottom: error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(errorLabel, BorderLayout.SOUTH);

        // ---------- Wire button ----------
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorLabel.setText(""); // clear previous error
                // Call the controller with the current course and topic
                String topic = topicField.getText().trim();
                if (topic.isEmpty()) {
                    errorLabel.setText("Please enter a topic.");
                    return;
                }
                controller.generate(courseId, topic);
            }
        });
    }

    /**
     * Called whenever the LectureNotesViewModel fires a property change.
     * We update the UI based on the new state.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LectureNotesState state = viewModel.getState();

        switch (evt.getPropertyName()) {
            case "notes":
            case "state":
                // On success, show the generated notes and clear errors
                notesArea.setText(state.getNotesText());
                errorLabel.setText(state.getError());
                break;
            case "error":
                // On failure, just show the error message
                errorLabel.setText(state.getError());
                break;
            default:
                // ignore other property names
        }
    }
}