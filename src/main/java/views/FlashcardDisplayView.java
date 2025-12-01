package views;

import entities.Flashcard;
import entities.FlashcardSet;
import interface_adapters.ViewManagerModel;
import interface_adapters.flashcards.FlashcardViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * View for studying flashcards with flip animation.
 * Displays one flashcard at a time with navigation controls.
 */
public class FlashcardDisplayView extends JPanel implements PropertyChangeListener {
  private final String viewName = "flashcardDisplay";
  private final FlashcardViewModel viewModel;
  private ViewManagerModel viewManagerModel;

  // UI Components
  private final JLabel titleLabel;
  private final JLabel progressLabel;
  private final JLabel sideIndicatorLabel;
  private final JPanel cardPanel;
  private final JTextArea cardTextArea;
  private final JButton flipButton;
  private final JButton prevButton;
  private final JButton nextButton;
  private final JButton backButton;

  /**
   * Constructs a FlashcardDisplayView with the given view model.
   *
   * @param viewModel the flashcard view model
   */
  public FlashcardDisplayView(FlashcardViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);

    // Initialize components
    this.titleLabel = new JLabel("Flashcard Study", SwingConstants.CENTER);
    this.progressLabel = new JLabel("Card - / -", SwingConstants.CENTER);
    this.sideIndicatorLabel = new JLabel("FRONT", SwingConstants.CENTER);
    this.cardPanel = new JPanel();
    this.cardTextArea = new JTextArea();
    this.flipButton = new JButton("Flip Card");
    this.prevButton = new JButton("← Previous");
    this.nextButton = new JButton("Next →");
    this.backButton = new JButton("Back to Generate");

    initializeUserInterface();
    addEventListeners();
  }

  /**
   * Initializes the user interface components.
   */
  private void initializeUserInterface() {
    this.setLayout(new BorderLayout(10, 10));
    this.setBorder(new EmptyBorder(20, 20, 20, 20));
    this.setPreferredSize(new Dimension(900, 700));
    this.setBackground(new Color(240, 240, 245));

    // === TOP PANEL - Title and Progress ===
    JPanel topPanel = new JPanel(new BorderLayout(5, 5));
    topPanel.setBackground(new Color(240, 240, 245));

    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setForeground(new Color(60, 60, 60));
    topPanel.add(titleLabel, BorderLayout.NORTH);

    progressLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
    progressLabel.setForeground(new Color(100, 100, 100));
    topPanel.add(progressLabel, BorderLayout.CENTER);

    this.add(topPanel, BorderLayout.NORTH);

    // === CENTER PANEL - Flashcard ===
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setBackground(new Color(240, 240, 245));

    // Card panel with shadow effect
    cardPanel.setLayout(new BorderLayout(10, 10));
    cardPanel.setBackground(Color.WHITE);
    cardPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
        new EmptyBorder(30, 30, 30, 30)
    ));
    cardPanel.setPreferredSize(new Dimension(600, 400));

    // Side indicator (FRONT/BACK)
    sideIndicatorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    sideIndicatorLabel.setForeground(new Color(100, 100, 100));
    cardPanel.add(sideIndicatorLabel, BorderLayout.NORTH);

    // Card text area
    cardTextArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
    cardTextArea.setLineWrap(true);
    cardTextArea.setWrapStyleWord(true);
    cardTextArea.setEditable(false);
    cardTextArea.setBackground(Color.WHITE);
    cardTextArea.setFocusable(false);
    cardTextArea.setBorder(new EmptyBorder(20, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane(cardTextArea);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    cardPanel.add(scrollPane, BorderLayout.CENTER);

    centerPanel.add(cardPanel);
    this.add(centerPanel, BorderLayout.CENTER);

    // === BOTTOM PANEL - Controls ===
    JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
    bottomPanel.setBackground(new Color(240, 240, 245));

    // Flip button (centered)
    JPanel flipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    flipPanel.setBackground(new Color(240, 240, 245));
    flipButton.setPreferredSize(new Dimension(150, 40));
    flipButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    flipButton.setBackground(new Color(70, 130, 180));
    flipButton.setForeground(Color.WHITE);
    flipButton.setFocusPainted(false);
    flipPanel.add(flipButton);
    bottomPanel.add(flipPanel, BorderLayout.CENTER);

    // Navigation panel (bottom)
    JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
    navigationPanel.setBackground(new Color(240, 240, 245));

    prevButton.setPreferredSize(new Dimension(120, 35));
    nextButton.setPreferredSize(new Dimension(120, 35));
    backButton.setPreferredSize(new Dimension(160, 35));

    navigationPanel.add(prevButton);
    navigationPanel.add(nextButton);
    navigationPanel.add(backButton);
    bottomPanel.add(navigationPanel, BorderLayout.SOUTH);

    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Adds event listeners to the buttons.
   */
  private void addEventListeners() {
    flipButton.addActionListener(e -> {
      boolean currentState = viewModel.isFlipped();
      viewModel.setFlipped(!currentState);
    });

    prevButton.addActionListener(e -> {
      int currentIndex = viewModel.getCurrentCardIndex();
      if (currentIndex > 0) {
        viewModel.setCurrentCardIndex(currentIndex - 1);
      }
    });

    nextButton.addActionListener(e -> {
      FlashcardSet set = viewModel.getCurrentFlashcardSet();
      if (set != null) {
        int currentIndex = viewModel.getCurrentCardIndex();
        if (currentIndex < set.size() - 1) {
          viewModel.setCurrentCardIndex(currentIndex + 1);
        }
      }
    });

    backButton.addActionListener(e -> {
      if (viewManagerModel != null) {
        viewManagerModel.setState("workspace");
        viewManagerModel.firePropertyChange();
      }
    });
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case FlashcardViewModel.FLASHCARDS_GENERATED:
        renderView();
        break;
      case FlashcardViewModel.CARD_CHANGED:
        renderView();
        break;
      case FlashcardViewModel.CARD_FLIPPED:
        renderView();
        break;
      case FlashcardViewModel.ERROR_OCCURRED:
        String error = viewModel.getErrorMessage();
        if (error != null) {
          JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      default:
        // Unknown property change, ignore
        break;
    }
  }

  /**
   * Renders the view based on the current state of the view model.
   */
  private void renderView() {
    FlashcardSet flashcardSet = viewModel.getCurrentFlashcardSet();

    // Check if flashcards are loaded
    if (flashcardSet == null || flashcardSet.size() == 0) {
      titleLabel.setText("No Flashcards Available");
      progressLabel.setText("Card - / -");
      cardTextArea.setText("Generate flashcards first!");
      sideIndicatorLabel.setText("");
      setNavigationEnabled(false);
      return;
    }

    // Update title
    titleLabel.setText("Flashcard Study: " + flashcardSet.getCourseName());

    // Update progress
    int currentIndex = viewModel.getCurrentCardIndex();
    int totalCards = flashcardSet.size();
    progressLabel.setText(String.format("Card %d / %d", currentIndex + 1, totalCards));

    // Get current flashcard
    Flashcard currentCard = flashcardSet.getCard(currentIndex);
    boolean isFlipped = viewModel.isFlipped();

    // Update card content
    if (isFlipped) {
      // Show back (answer)
      cardTextArea.setText(currentCard.getAnswer());
      sideIndicatorLabel.setText("BACK (Answer)");
      sideIndicatorLabel.setForeground(new Color(34, 139, 34));
      cardPanel.setBackground(new Color(245, 255, 245)); // Light green
    } else {
      // Show front (question)
      cardTextArea.setText(currentCard.getQuestion());
      sideIndicatorLabel.setText("FRONT (Question)");
      sideIndicatorLabel.setForeground(new Color(70, 130, 180));
      cardPanel.setBackground(Color.WHITE);
    }

    cardTextArea.setCaretPosition(0); // Scroll to top

    // Update navigation buttons
    prevButton.setEnabled(currentIndex > 0);
    nextButton.setEnabled(currentIndex < totalCards - 1);
    setNavigationEnabled(true);
  }

  /**
   * Sets the enabled state of navigation buttons.
   *
   * @param enabled whether navigation should be enabled
   */
  private void setNavigationEnabled(boolean enabled) {
    flipButton.setEnabled(enabled);
    prevButton.setEnabled(enabled);
    nextButton.setEnabled(enabled);
    backButton.setEnabled(true); // Always enabled
  }

  /**
   * Sets the view manager model for this view.
   *
   * @param viewManagerModel the view manager model
   */
  public void setViewManagerModel(ViewManagerModel viewManagerModel) {
    this.viewManagerModel = viewManagerModel;
  }

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return viewName;
  }
}
