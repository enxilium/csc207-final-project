// Assumes a package structure like this. Adjust to your own.
package views;

import interface_adapters.ViewManagerModel;
import interface_adapters.evaluate_test.EvaluateTestPresenter;
import interface_adapters.evaluate_test.EvaluateTestState;
import interface_adapters.evaluate_test.EvaluateTestViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for displaying the results of a mock test evaluation.
 * It listens to the EvaluateTestViewModel and renders the state, allowing the user
 * to navigate through their graded answers.
 */
public class EvaluateTestView extends JPanel implements PropertyChangeListener {

    // --- Core Components ---
    private final EvaluateTestViewModel viewModel;
    private EvaluateTestPresenter presenter = null;

    // --- Swing UI Components ---
    private final JLabel scoreLabel;
    private final JLabel progressLabel;
    private final JLabel correctnessIndicator;
    private final JTextArea questionArea;
    private final JTextArea userAnswerArea;
    private final JTextArea correctAnswerArea;
    private final JTextArea feedbackArea;
    private final JButton prevButton;
    private final JButton nextButton;
    private final JButton finishButton;

    public EvaluateTestView(EvaluateTestViewModel viewModel) {
        this.viewModel = viewModel;

        // The View subscribes to changes from the ViewModel.
        this.viewModel.addPropertyChangeListener(this);

        // Initialize UI components
        this.scoreLabel = new JLabel("Score: --%", SwingConstants.CENTER);
        this.progressLabel = new JLabel("Question - / -", SwingConstants.CENTER);
        this.correctnessIndicator = new JLabel("N/A", SwingConstants.CENTER);
        this.questionArea = createReadOnlyTextArea();
        this.userAnswerArea = createReadOnlyTextArea();
        this.correctAnswerArea = createReadOnlyTextArea();
        this.feedbackArea = createReadOnlyTextArea();
        this.prevButton = new JButton("Previous");
        this.nextButton = new JButton("Next");
        this.finishButton = new JButton("Finish Review");

        initializeUI();
        addEventListeners();
        renderView(); // Initial render for the "empty" state
    }

    public void setPresenter(EvaluateTestPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * This method is called by the PropertyChangeSupport system whenever the
     * ViewModel fires a property change. It's the trigger to update the UI.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // The ViewModel's state has changed, so re-render the entire view
        // to reflect the latest data.
        renderView();
    }

    /**
     * The main rendering method. It reads the current state from the ViewModel
     * and updates all Swing components. This is where deconstruction of the
     * lists into individual strings happens.
     */
    private void renderView() {
        EvaluateTestState state = viewModel.getState();
        int index = state.getCurrentQuestionIndex();

        // State: No evaluation data is loaded yet
        if (state.getQuestions() == null || state.getQuestions().isEmpty()) {
            scoreLabel.setText("Awaiting Evaluation Results...");
            progressLabel.setText("Question - / -");
            correctnessIndicator.setText("N/A");
            questionArea.setText("");
            userAnswerArea.setText("");
            correctAnswerArea.setText("");
            feedbackArea.setText("");
            setNavigationEnabled(false);
            return;
        }

        // --- RENDER STATIC COMPONENTS (like the overall score) ---
        scoreLabel.setText("Your Score: " + state.getScore() + "%");

        // --- DECONSTRUCTION STEP ---
        // The View pulls specific items from the state's lists based on the current index.
        String questionText = state.getQuestions().get(index);
        String userAnswerText = state.getUserAnswers().get(index);
        String correctAnswerText = state.getAnswers().get(index);
        String feedbackText = state.getFeedback().get(index);
        String correctnessValue = state.getCorrectness().get(index);
        String progressText = "Question " + (index + 1) + " / " + state.getQuestions().size();

        // --- UPDATE UI COMPONENTS WITH DECONSTRUCTED DATA ---
        progressLabel.setText(progressText);
        questionArea.setText(questionText);
        userAnswerArea.setText(userAnswerText);
        correctAnswerArea.setText(correctAnswerText);
        feedbackArea.setText(feedbackText);

        // Update the correctness indicator with text and color
        switch (correctnessValue) {
            case "1":
                correctnessIndicator.setText("Correct");
                correctnessIndicator.setForeground(new Color(0, 150, 0)); // Dark Green
                break;
            case "0.5":
                correctnessIndicator.setText("Partially Correct");
                correctnessIndicator.setForeground(new Color(255, 140, 0)); // Orange
                break;
            case "0":
                correctnessIndicator.setText("Incorrect");
                correctnessIndicator.setForeground(Color.RED);
                break;
            default:
                correctnessIndicator.setText("N/A");
                correctnessIndicator.setForeground(Color.BLACK);
                break;
        }

        setNavigationEnabled(true);
        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < state.getQuestions().size() - 1);
    }

    private void setNavigationEnabled(boolean enabled) {
        prevButton.setEnabled(enabled);
        nextButton.setEnabled(enabled);
        finishButton.setEnabled(enabled);
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- Top Panel for Score ---
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.add(scoreLabel, BorderLayout.NORTH);

        // --- Center Panel for Detailed Review ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        progressLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        correctnessIndicator.setFont(new Font("SansSerif", Font.BOLD, 16));

        centerPanel.add(progressLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(createLabeledScrollPane("Question:", questionArea));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(correctnessIndicator);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(createLabeledScrollPane("Your Answer:", userAnswerArea));
        centerPanel.add(createLabeledScrollPane("Correct Answer:", correctAnswerArea));
        centerPanel.add(createLabeledScrollPane("Feedback:", feedbackArea));
        this.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel for Navigation ---
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(finishButton);
        this.add(navigationPanel, BorderLayout.SOUTH);
    }

    private JScrollPane createLabeledScrollPane(String labelText, JTextArea textArea) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Using a wrapper panel to keep label and scroll pane together
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(label);
        wrapper.add(scrollPane);
        return scrollPane; // Returning scrollpane directly might be better if label is outside
    }


    private JTextArea createReadOnlyTextArea() {
        JTextArea textArea = new JTextArea(3, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return textArea;
    }

    private void addEventListeners() {
        // --- Navigation actions are simple UI state changes handled by the Presenter ---
        nextButton.addActionListener(e -> presenter.goToNextQuestion());
        prevButton.addActionListener(e -> presenter.goToPreviousQuestion());
    }
}