package views;

import com.google.gson.Gson;
import entities.FlashcardSet;
import interface_adapters.ViewManagerModel;
import interface_adapters.lecturenotes.LectureNotesViewModel;
import interface_adapters.lecturenotes.LectureNotesState;
import interface_adapters.flashcards.FlashcardViewModel;
import interface_adapters.evaluate_test.EvaluateTestViewModel;
import interface_adapters.evaluate_test.EvaluateTestState;
import interface_adapters.mock_test.MockTestViewModel;
import interface_adapters.mock_test.MockTestState;
import interface_adapters.timeline.TimelineController;
import interface_adapters.timeline.ViewTimelineViewModel;
import usecases.Timeline.ViewTimelineResponse;
import usecases.evaluate_test.EvaluateTestOutputData;
import usecases.mock_test_generation.MockTestGenerationOutputData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewTimelineView extends JPanel implements PropertyChangeListener {
    private static final String TIMELINE_PROPERTY = "timeline";
    private static final String ERROR_TITLE = "Error";
    private static final String NON_DIGIT_PATTERN = "\\D+";
    
    private final ViewTimelineViewModel vm;
    private final ViewManagerModel viewManagerModel;
    private final LectureNotesViewModel lectureNotesViewModel;
    private final FlashcardViewModel flashcardViewModel;
    private final EvaluateTestViewModel evaluateTestViewModel;
    private final MockTestViewModel mockTestViewModel;
    private final DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = new DefaultListModel<>();
    private final JList<ViewTimelineResponse.TimelineCardVM> list = new JList<>(listModel);
    private final JLabel emptyLabel = new JLabel("This page is empty", SwingConstants.CENTER);
    private final Gson gson = new Gson();

    public ViewTimelineView(ViewTimelineViewModel vm, TimelineController controller, 
                           ViewManagerModel viewManagerModel,
                           LectureNotesViewModel lectureNotesViewModel,
                           FlashcardViewModel flashcardViewModel,
                           EvaluateTestViewModel evaluateTestViewModel,
                           MockTestViewModel mockTestViewModel) {
        this.vm = vm;
        this.viewManagerModel = viewManagerModel;
        this.lectureNotesViewModel = lectureNotesViewModel;
        this.flashcardViewModel = flashcardViewModel;
        this.evaluateTestViewModel = evaluateTestViewModel;
        this.mockTestViewModel = mockTestViewModel;
        this.vm.addPropertyChangeListener(this);

        setLayout(new BorderLayout(8, 8));
        var header = new JPanel(new BorderLayout());
        
        // Back button to return to workspace
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            viewManagerModel.setState("workspace");
            viewManagerModel.firePropertyChange();
        });
        header.add(backButton, BorderLayout.WEST);
        
        // History label in center
        header.add(new JLabel("History", SwingConstants.CENTER), BorderLayout.CENTER);
        
        // Refresh button on the right
        JButton refreshBtn = new JButton("Refresh");
        header.add(refreshBtn, BorderLayout.EAST);

        list.setCellRenderer(new TimelineCardRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add mouse listener to open study materials when items are clicked
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0 && index < listModel.getSize()) {
                    ViewTimelineResponse.TimelineCardVM card = listModel.getElementAt(index);
                    // Verify contentId is set
                    if (card.getContentId() == null) {
                        // Try to get from viewModel if missing
                        List<ViewTimelineResponse.TimelineCardVM> items = vm.getItems();
                        if (index < items.size()) {
                            card = items.get(index);
                        }
                    }
                    // Open on double-click or single-click
                    if (e.getClickCount() >= 1 && card.getContentId() != null) {
                        openStudyMaterial(card);
                    }
                }
            }
        });

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);

        emptyLabel.setVisible(false);

        final TimelineController controllerRef = controller;
        refreshBtn.addActionListener(e -> {
            if (vm.getCourseId() != null) controllerRef.open(vm.getCourseId());
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!TIMELINE_PROPERTY.equals(evt.getPropertyName())) return;

        listModel.clear();
        for (ViewTimelineResponse.TimelineCardVM card : vm.getItems()) {
            listModel.addElement(card);
        }

        emptyLabel.setVisible(vm.isEmpty());
        revalidate();
        repaint();
    }

    private static class TimelineCardRenderer extends JPanel implements ListCellRenderer<ViewTimelineResponse.TimelineCardVM> {
        private final JLabel title = new JLabel();
        private final JLabel subtitle = new JLabel();
        private final JLabel time = new JLabel();

        public TimelineCardRenderer() {
            setLayout(new BorderLayout(4, 4));
            var top = new JPanel(new BorderLayout());
            title.setFont(title.getFont().deriveFont(Font.BOLD));
            subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
            time.setFont(time.getFont().deriveFont(Font.PLAIN, 11f));
            top.add(title, BorderLayout.WEST);
            top.add(time, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);
            add(subtitle, BorderLayout.SOUTH);
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ViewTimelineResponse.TimelineCardVM> list,
                ViewTimelineResponse.TimelineCardVM value,
                int index, boolean isSelected, boolean cellHasFocus) {

            title.setText(value.getTitle() != null ? value.getTitle() : value.getType());
            String sub = (value.getSubtitle() == null || value.getSubtitle().isEmpty())
                    ? (value.getSnippet() == null ? "" : value.getSnippet())
                    : value.getSubtitle();
            subtitle.setText(sub);
            time.setText(value.getTime() != null ? value.getTime() : "");

            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }

    /**
     * Opens the appropriate study material view based on the card type.
     * @param card The timeline card representing the study material
     */
    private void openStudyMaterial(ViewTimelineResponse.TimelineCardVM card) {
        if (card.getContentId() == null) {
            showError("Unable to open: content ID is missing.");
            return;
        }

        if (card.getType() == null) {
            showError("Unable to open: content type is missing.");
            return;
        }

        switch (card.getType()) {
            case "NOTES":
                openNotes(card);
                break;
            case "FLASHCARDS":
                openFlashcards(card);
                break;
            case "QUIZ":
                openQuiz(card);
                break;
            default:
                showError("Unknown content type: " + card.getType());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    private void openNotes(ViewTimelineResponse.TimelineCardVM card) {
        String notesContent = card.getFullNotesText() != null && !card.getFullNotesText().isEmpty()
            ? card.getFullNotesText() : card.getSnippet();
        String notesTitle = card.getTitle() != null && !card.getTitle().isEmpty() ? card.getTitle() : "Notes";
        
        LectureNotesState notesState = lectureNotesViewModel.getState();
        notesState.setNotesText(notesContent != null ? notesContent : "");
        notesState.setTopic(notesTitle);
        notesState.setError("");
        notesState.setLoading(false);
        lectureNotesViewModel.setState(notesState);
        
        navigateToView(lectureNotesViewModel.getViewName());
    }

    private void openFlashcards(ViewTimelineResponse.TimelineCardVM card) {
        FlashcardSet flashcardSet = deserializeFlashcards(card.getFlashcardData());
        
        if (flashcardSet != null) {
            flashcardViewModel.setCurrentFlashcardSet(flashcardSet);
            navigateToView("flashcardDisplay");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Flashcard data is not available.",
                    ERROR_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private FlashcardSet deserializeFlashcards(String jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            return null;
        }
        try {
            return gson.fromJson(jsonData, FlashcardSet.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void openQuiz(ViewTimelineResponse.TimelineCardVM card) {
        if (card.getEvaluationData() != null && !card.getEvaluationData().isEmpty()) {
            openSubmittedQuiz(card.getEvaluationData());
        } else if (card.getTestData() != null && !card.getTestData().isEmpty()) {
            openGeneratedQuiz(card.getTestData());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Quiz data is not available.",
                    ERROR_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openSubmittedQuiz(String jsonData) {
        try {
            EvaluateTestOutputData evaluationData = gson.fromJson(jsonData, EvaluateTestOutputData.class);
            if (evaluationData != null) {
                setEvaluationState(evaluationData);
                navigateToView(evaluateTestViewModel.getViewName());
            }
        } catch (Exception e) {
            showError("Failed to load evaluation data.");
        }
    }

    private void openGeneratedQuiz(String jsonData) {
        try {
            MockTestGenerationOutputData testData = gson.fromJson(jsonData, MockTestGenerationOutputData.class);
            if (testData != null) {
                setTestState(testData);
                navigateToView(mockTestViewModel.getViewName());
            }
        } catch (Exception e) {
            showError("Failed to load test data.");
        }
    }

    private void setEvaluationState(EvaluateTestOutputData evaluationData) {
        EvaluateTestState evalState = evaluateTestViewModel.getState();
        evalState.setQuestions(toSafeList(evaluationData.getQuestions()));
        evalState.setAnswers(toSafeList(evaluationData.getAnswers()));
        evalState.setUserAnswers(toSafeList(evaluationData.getUserAnswers()));
        evalState.setCorrectness(toSafeList(evaluationData.getCorrectness()));
        evalState.setFeedback(toSafeList(evaluationData.getFeedback()));
        evalState.setScore(evaluationData.getScore());
        evalState.setCurrentQuestionIndex(0);
        evaluateTestViewModel.setState(evalState);
        evaluateTestViewModel.firePropertyChange();
    }

    private void setTestState(MockTestGenerationOutputData testData) {
        MockTestState testState = mockTestViewModel.getState();
        testState.setQuestions(toSafeList(testData.getQuestions()));
        testState.setAnswers(toSafeList(testData.getAnswers()));
        testState.setChoices(toSafeListOfLists(testData.getChoices()));
        testState.setQuestionTypes(toSafeList(testData.getQuestionTypes()));
        testState.setCurrentQuestionIndex(0);
        testState.initializeUserAnswers(testState.getQuestions().size());
        mockTestViewModel.setState(testState);
        mockTestViewModel.firePropertyChange();
    }

    private <T> List<T> toSafeList(List<T> list) {
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private List<List<String>> toSafeListOfLists(List<List<String>> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<List<String>> result = new ArrayList<>();
        for (List<String> innerList : list) {
            result.add(innerList != null ? new ArrayList<>(innerList) : new ArrayList<>());
        }
        return result;
    }

    private void navigateToView(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChange();
    }

    public String getViewName() { return vm.getViewName(); }
}

