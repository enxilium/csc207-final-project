package views;

import interface_adapters.evaluate_test.EvaluateTestController;
import interface_adapters.mock_test.MockTestPresenter;
import interface_adapters.mock_test.MockTestState;
import interface_adapters.mock_test.MockTestViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteTestView extends JPanel implements PropertyChangeListener {

    // --- Core Components ---
    private final MockTestViewModel viewModel;
    private MockTestPresenter presenter = null;
    private EvaluateTestController controller = null;

    // --- UI Components ---
    private final JLabel progressLabel;
    private final JLabel questionLabel;
    private final JButton prevButton;
    private final JButton nextButton;
    private final JButton submitButton;

    // --- Dynamic Answer Panel Components ---
    private final JPanel answerContainerPanel; // The panel with CardLayout
    private final CardLayout answerCardLayout;
    private JTextField shortAnswerField;
    private ButtonGroup multipleChoiceGroup;
    private ButtonGroup trueFalseGroup;
    private JPanel multipleChoicePanel;
    private JPanel trueFalsePanel;

    // Constants for the CardLayout panel names
    private static final String SHORT_ANSWER_PANEL = "Short Answer";
    private static final String MULTIPLE_CHOICE_PANEL = "Multiple Choice";
    private static final String TRUE_FALSE_PANEL = "True/False"; // Often a variant of MCQ
    private static final Pattern MULTIPLE_CHOICE_OPTION_PATTERN =
        Pattern.compile("(?m)^[A-D][\\).]\\s*(.+)");

    public WriteTestView(MockTestViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // Initialize components
        this.progressLabel = new JLabel("No test loaded.", SwingConstants.CENTER);
        this.questionLabel = new JLabel("Please generate a test to begin.", SwingConstants.CENTER);
        this.prevButton = new JButton("Previous");
        this.nextButton = new JButton("Next");
        this.submitButton = new JButton("Submit Test");

        // Setup for the dynamic answer area
        this.answerCardLayout = new CardLayout();
        this.answerContainerPanel = new JPanel(answerCardLayout);

        initializeUI();
        addEventListeners();
        renderView();
    }

    public void setPresenter(MockTestPresenter presenter) {
        this.presenter = presenter;
    }

    public void setController(EvaluateTestController controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // When the state changes, re-render the entire view
        renderView();
    }

    private void renderView() {
        MockTestState state = viewModel.getState();
        int index = state.getCurrentQuestionIndex();
        List<String> questions = state.getQuestions();
        List<String> questionTypes = state.getQuestionTypes();
    List<String> userAnswers = state.getUserAnswers();
    List<List<String>> choicesMatrix = state.getChoices();

        boolean hasLoadedTest = index >= 0
                && questions != null
                && questionTypes != null
                && index < questions.size()
                && index < questionTypes.size();

        if (!hasLoadedTest) {
            progressLabel.setText("No test loaded.");
            questionLabel.setText("Please generate a test to begin.");
            answerCardLayout.show(answerContainerPanel, SHORT_ANSWER_PANEL);
            setAnswerPanelsEnabled(false);
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            submitButton.setEnabled(false);
            shortAnswerField.setText("");
            clearButtonGroupSelections();
            return;
        }

        String fullQuestionText = questions.get(index);
        String questionType = questionTypes.get(index);
        String progressText = "Question " + (index + 1) + " / " + questions.size();
        String currentAnswer = (userAnswers != null && index < userAnswers.size())
                ? userAnswers.get(index)
                : "";

        progressLabel.setText(progressText);
        setAnswerPanelsEnabled(true);
        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);
        submitButton.setEnabled(true);

        String cardName = resolveCardName(questionType);
        answerCardLayout.show(answerContainerPanel, cardName);

        switch (cardName) {
            case SHORT_ANSWER_PANEL:
                questionLabel.setText(formatQuestionLabel(fullQuestionText));
                if (!shortAnswerField.getText().equals(currentAnswer)) {
                    shortAnswerField.setText(currentAnswer);
                }
                clearButtonGroupSelections();
                break;

            case MULTIPLE_CHOICE_PANEL:
                questionLabel.setText(formatQuestionLabel(extractQuestionPrompt(fullQuestionText)));
                List<String> options = getChoicesForIndex(choicesMatrix, index);
                refreshMultipleChoiceOptions(options, fullQuestionText, currentAnswer);
                break;

            case TRUE_FALSE_PANEL:
                questionLabel.setText(formatQuestionLabel(extractQuestionPrompt(fullQuestionText)));
                ButtonGroup group = getButtonGroupForType(cardName);
                if (group != null) {
                    group.clearSelection();
                    applyStoredAnswer(group, currentAnswer);
                }
                break;
        }
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        progressLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        // --- Create the individual answer panels ("cards") ---
        answerContainerPanel.add(createShortAnswerPanel(), SHORT_ANSWER_PANEL);

        multipleChoicePanel = createEmptyChoicePanel();
        answerContainerPanel.add(multipleChoicePanel, MULTIPLE_CHOICE_PANEL);

        trueFalsePanel = createEmptyChoicePanel();
        answerContainerPanel.add(trueFalsePanel, TRUE_FALSE_PANEL);
        populateTrueFalseOptions();


        // --- Assemble Main View ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(answerContainerPanel);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(submitButton);

        this.add(progressLabel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(navigationPanel, BorderLayout.SOUTH);
    }

    private JPanel createShortAnswerPanel() {
        JPanel panel = new JPanel();
        shortAnswerField = new JTextField(40);
        shortAnswerField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(shortAnswerField);
        return panel;
    }

    private JPanel createEmptyChoicePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void addEventListeners() {
        // --- Navigation handled by Presenter ---
        nextButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.goToNextQuestion();
            }
        });
        prevButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.goToPreviousQuestion();
            }
        });

        // --- Use case trigger handled by Controller ---
        submitButton.addActionListener(e -> {
            if (controller == null) {
                return;
            }
            MockTestState currentState = viewModel.getState();
            controller.execute(
                    currentState.getCourseId(),
                    currentState.getUserAnswers(),
                    currentState.getQuestions(),
                    currentState.getAnswers());
        });

        // --- State updates for answer fields ---
        shortAnswerField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStateFromAnswer(); }
            public void removeUpdate(DocumentEvent e) { updateStateFromAnswer(); }
            public void changedUpdate(DocumentEvent e) { updateStateFromAnswer(); }
        });
    }

    /**
     * A single method to get the current answer from WHICHEVER panel is visible
     * and update the state.
     */
    private void updateStateFromAnswer() {
        MockTestState state = viewModel.getState();
        int index = state.getCurrentQuestionIndex();
        List<String> questionTypes = state.getQuestionTypes();

        if (index < 0 || questionTypes == null || index >= questionTypes.size()) {
            return;
        }

        String questionType = resolveCardName(questionTypes.get(index));
        String answer = "";

        if (SHORT_ANSWER_PANEL.equals(questionType)) {
            answer = shortAnswerField.getText();
        } else { // Multiple Choice or True/False
            ButtonGroup group = getButtonGroupForType(questionType);
            if (group == null) {
                state.setUserAnswerForCurrentQuestion("");
                return;
            }
            ButtonModel selectedButton = group.getSelection();
            if (selectedButton != null) {
                answer = selectedButton.getActionCommand();
            }
        }
        state.setUserAnswerForCurrentQuestion(answer);
    }

    private void setAnswerPanelsEnabled(boolean isEnabled) {
        if (shortAnswerField != null) {
            shortAnswerField.setEnabled(isEnabled);
        }
        setGroupEnabled(multipleChoiceGroup, isEnabled);
        setGroupEnabled(trueFalseGroup, isEnabled);
    }

    private void setGroupEnabled(ButtonGroup group, boolean isEnabled) {
        if (group == null) {
            return;
        }
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            buttons.nextElement().setEnabled(isEnabled);
        }
    }

    private void addGroupActionListeners(ButtonGroup group) {
        if (group == null) {
            return;
        }
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            buttons.nextElement().addActionListener(e -> updateStateFromAnswer());
        }
    }

    private void clearButtonGroupSelections() {
        clearGroupSelection(multipleChoiceGroup);
        clearGroupSelection(trueFalseGroup);
    }

    private void clearGroupSelection(ButtonGroup group) {
        if (group != null) {
            group.clearSelection();
        }
    }

    private ButtonGroup getButtonGroupForType(String questionType) {
        if (MULTIPLE_CHOICE_PANEL.equalsIgnoreCase(questionType)) {
            return multipleChoiceGroup;
        }
        if (TRUE_FALSE_PANEL.equalsIgnoreCase(questionType)) {
            return trueFalseGroup;
        }
        return null;
    }

    private String resolveCardName(String questionType) {
        if (questionType == null) {
            return SHORT_ANSWER_PANEL;
        }
        if (SHORT_ANSWER_PANEL.equalsIgnoreCase(questionType) || "Essay".equalsIgnoreCase(questionType)) {
            return SHORT_ANSWER_PANEL;
        }
        if (MULTIPLE_CHOICE_PANEL.equalsIgnoreCase(questionType)) {
            return MULTIPLE_CHOICE_PANEL;
        }
        if (TRUE_FALSE_PANEL.equalsIgnoreCase(questionType)) {
            return TRUE_FALSE_PANEL;
        }
        return SHORT_ANSWER_PANEL;
    }

    private void applyStoredAnswer(ButtonGroup group, String currentAnswer) {
        if (group == null) {
            return;
        }
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.getActionCommand().equals(currentAnswer)) {
                button.setSelected(true);
                break;
            }
        }
    }

    private String formatQuestionLabel(String text) {
        return "<html><body style='text-align: center; width: 400px;'>" + text + "</body></html>";
    }

    private void refreshMultipleChoiceOptions(List<String> parsedChoices, String fallbackQuestionText,
                                              String currentAnswer) {
        List<String> options = parsedChoices;
        if (options == null || options.isEmpty()) {
            options = parseChoicesFromQuestionText(fallbackQuestionText);
        }
        if (options == null || options.isEmpty()) {
            options = defaultChoicePlaceholders();
        }

        multipleChoicePanel.removeAll();
        multipleChoiceGroup = new ButtonGroup();

        for (int i = 0; i < options.size(); i++) {
            char choiceLetter = (char) ('A' + i);
            String displayLabel = choiceLetter + ". " + options.get(i);
            JRadioButton radioButton = new JRadioButton(displayLabel);
            radioButton.setActionCommand(String.valueOf(choiceLetter));
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            multipleChoiceGroup.add(radioButton);
            multipleChoicePanel.add(radioButton);
        }

        addGroupActionListeners(multipleChoiceGroup);
        applyStoredAnswer(multipleChoiceGroup, currentAnswer);
        multipleChoicePanel.revalidate();
        multipleChoicePanel.repaint();
    }

    private List<String> defaultChoicePlaceholders() {
        List<String> defaults = new ArrayList<>();
        defaults.add("Option A");
        defaults.add("Option B");
        defaults.add("Option C");
        defaults.add("Option D");
        return defaults;
    }

    private List<String> getChoicesForIndex(List<List<String>> choicesMatrix, int index) {
        if (choicesMatrix == null || index < 0 || index >= choicesMatrix.size()) {
            return Collections.emptyList();
        }
        List<String> choices = choicesMatrix.get(index);
        return choices == null ? Collections.emptyList() : choices;
    }

    private String extractQuestionPrompt(String questionText) {
        if (questionText == null || questionText.isEmpty()) {
            return "";
        }
        String[] parts = questionText.split("\\R", -1);
        return parts.length > 0 ? parts[0] : questionText;
    }

    private List<String> parseChoicesFromQuestionText(String questionText) {
        if (questionText == null || questionText.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> options = new ArrayList<>();
        Matcher matcher = MULTIPLE_CHOICE_OPTION_PATTERN.matcher(questionText);
        while (matcher.find()) {
            options.add(matcher.group(1).trim());
        }
        return options;
    }

    private void populateTrueFalseOptions() {
        trueFalsePanel.removeAll();
        trueFalseGroup = new ButtonGroup();
        addChoiceButton(trueFalsePanel, trueFalseGroup, "True", "True");
        addChoiceButton(trueFalsePanel, trueFalseGroup, "False", "False");
        addGroupActionListeners(trueFalseGroup);
        trueFalsePanel.revalidate();
        trueFalsePanel.repaint();
    }

    private void addChoiceButton(JPanel panel, ButtonGroup group, String displayText, String actionCommand) {
        JRadioButton button = new JRadioButton(displayText);
        button.setActionCommand(actionCommand);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(button);
        panel.add(button);
    }
}