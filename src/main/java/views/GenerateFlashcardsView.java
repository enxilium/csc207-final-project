package views;

import interface_adapters.flashcards.FlashcardViewModel;
import interface_adapters.flashcards.GenerateFlashcardsController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * View for generating flashcards from course materials.
 * Provides input fields for course name and topic/content.
 */
public class GenerateFlashcardsView extends JPanel implements PropertyChangeListener {
  private final String viewName = "generateFlashcards";
  private final FlashcardViewModel viewModel;
  private GenerateFlashcardsController controller;

  // UI Components
  private final JLabel titleLabel;
  private final JTextField courseNameField;
  private final JTextField topicField;
  private final JButton generateButton;
  private final JButton backButton;
  private final JLabel statusLabel;

  /**
   * Constructs a GenerateFlashcardsView with the given view model.
   *
   * @param viewModel the flashcard view model
   */
  public GenerateFlashcardsView(FlashcardViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);

    // Initialize components
    this.titleLabel = new JLabel("Generate Flashcards", SwingConstants.CENTER);
    this.courseNameField = new JTextField(20);
    this.topicField = new JTextField(30);
    this.generateButton = new JButton("Generate Flashcards");
    this.backButton = new JButton("Back to Workspace");
    this.statusLabel = new JLabel("", SwingConstants.CENTER);

    initializeUserInterface();
    addEventListeners();
  }

  /**
   * Initializes the user interface components.
   */
  private void initializeUserInterface() {
    this.setLayout(new BorderLayout(10, 10));
    this.setBorder(new EmptyBorder(20, 20, 20, 20));
    this.setPreferredSize(new Dimension(800, 600));
    this.setBackground(new Color(240, 240, 245));

    // === TOP PANEL - Title ===
    JPanel topPanel = new JPanel();
    topPanel.setBackground(new Color(240, 240, 245));
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setForeground(new Color(60, 60, 60));
    topPanel.add(titleLabel);
    this.add(topPanel, BorderLayout.NORTH);

    // === CENTER PANEL - Input Form ===
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.setBackground(Color.WHITE);
    centerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        new EmptyBorder(30, 30, 30, 30)
    ));

    // Course Name Field
    JPanel courseNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    courseNamePanel.setBackground(Color.WHITE);
    JLabel courseNameLabel = new JLabel("Course Name:");
    courseNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    courseNamePanel.add(courseNameLabel);
    courseNamePanel.add(courseNameField);
    centerPanel.add(courseNamePanel);

    centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Topic Field
    JPanel topicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topicPanel.setBackground(Color.WHITE);
    JLabel topicLabel = new JLabel("Topic/Content:");
    topicLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    topicPanel.add(topicLabel);
    topicPanel.add(topicField);
    centerPanel.add(topicPanel);

    centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Instructions
    JTextArea instructions = new JTextArea(
        "Enter the course name and a topic or description.\n"
            + "The system will generate flashcards based on your course materials."
    );
    instructions.setEditable(false);
    instructions.setLineWrap(true);
    instructions.setWrapStyleWord(true);
    instructions.setBackground(Color.WHITE);
    instructions.setFont(new Font("SansSerif", Font.PLAIN, 12));
    instructions.setForeground(new Color(100, 100, 100));
    centerPanel.add(instructions);

    this.add(centerPanel, BorderLayout.CENTER);

    // === BOTTOM PANEL - Buttons and Status ===
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(new Color(240, 240, 245));

    // Status label
    statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
    statusLabel.setForeground(new Color(100, 100, 100));
    bottomPanel.add(statusLabel, BorderLayout.NORTH);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttonPanel.setBackground(new Color(240, 240, 245));

    generateButton.setPreferredSize(new Dimension(180, 40));
    generateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
    generateButton.setBackground(new Color(70, 130, 180));
    generateButton.setForeground(Color.WHITE);
    generateButton.setFocusPainted(false);

    backButton.setPreferredSize(new Dimension(150, 40));

    buttonPanel.add(generateButton);
    buttonPanel.add(backButton);
    bottomPanel.add(buttonPanel, BorderLayout.CENTER);

    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Adds event listeners to the buttons.
   */
  private void addEventListeners() {
    generateButton.addActionListener(e -> {
      String courseName = courseNameField.getText().trim();
      String topic = topicField.getText().trim();

      // Validation
      if (courseName.isEmpty() || topic.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter both course name and topic",
            "Input Required",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      // Use controller to trigger flashcard generation
      if (controller != null) {
        statusLabel.setText("Generating flashcards...");
        viewModel.setLoading(true);

        // Run in background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() {
            controller.generateFlashcards(courseName, topic);
            return null;
          }
        };
        worker.execute();
      } else {
        JOptionPane.showMessageDialog(this,
            "Controller not initialized",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    backButton.addActionListener(e -> {
      // Navigate back to workspace
      // This will be handled by ViewManager
      firePropertyChange("navigateToWorkspace", null, null);
    });
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case FlashcardViewModel.FLASHCARDS_GENERATED:
        statusLabel.setText("Flashcards generated successfully!");
        viewModel.setLoading(false);
        // Auto-navigate to display view could be handled here
        break;

      case FlashcardViewModel.ERROR_OCCURRED:
        String error = viewModel.getErrorMessage();
        // Only show error dialog if there's actually an error
        if (error != null && !error.isEmpty()) {
          statusLabel.setText("Error: " + error);
          viewModel.setLoading(false);
          JOptionPane.showMessageDialog(this,
              error,
              "Generation Error",
              JOptionPane.ERROR_MESSAGE);
        }
        break;

      case FlashcardViewModel.LOADING_CHANGED:
        boolean isLoading = viewModel.isLoading();
        generateButton.setEnabled(!isLoading);
        courseNameField.setEnabled(!isLoading);
        topicField.setEnabled(!isLoading);
        if (!isLoading) {
          statusLabel.setText("");
        }
        break;

      default:
        // Unknown property change, ignore
        break;
    }
  }

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return viewName;
  }

  /**
   * Sets the controller for this view.
   *
   * @param controller the flashcard generation controller
   */
  public void setController(GenerateFlashcardsController controller) {
    this.controller = controller;
  }
}
