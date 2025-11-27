package Timeline;

import interface_adapters.ViewManagerModel;
import views.NotesView;
import views.FlashcardsView;
import views.QuizView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ViewTimelineView extends JPanel implements PropertyChangeListener {
    private static final String TIMELINE_PROPERTY = "timeline";
    private static final String ERROR_TITLE = "Error";
    private static final String NOTES_VIEW_NAME = "notes";
    private static final String FLASHCARDS_VIEW_NAME = "flashcards";
    private static final String QUIZ_VIEW_NAME = "quiz";
    private static final String NON_DIGIT_PATTERN = "\\D+";
    
    private final ViewTimelineViewModel vm;
    private final ViewManagerModel viewManagerModel;
    private final NotesView notesView;
    private final FlashcardsView flashcardsView;
    private final QuizView quizView;
    private final DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = new DefaultListModel<>();
    private final JList<ViewTimelineResponse.TimelineCardVM> list = new JList<>(listModel);
    private final JLabel emptyLabel = new JLabel("This page is empty", SwingConstants.CENTER);

    public ViewTimelineView(ViewTimelineViewModel vm, TimelineController controller, 
                           ViewManagerModel viewManagerModel, NotesView notesView, 
                           FlashcardsView flashcardsView, QuizView quizView) {
        this.vm = vm;
        this.viewManagerModel = viewManagerModel;
        this.notesView = notesView;
        this.flashcardsView = flashcardsView;
        this.quizView = quizView;
        this.vm.addPropertyChangeListener(this);
        
        // Set up back buttons to return to timeline
        notesView.getBackButton().addActionListener(e -> {
            viewManagerModel.setState(TIMELINE_PROPERTY);
            viewManagerModel.firePropertyChange();
        });
        flashcardsView.getBackButton().addActionListener(e -> {
            viewManagerModel.setState(TIMELINE_PROPERTY);
            viewManagerModel.firePropertyChange();
        });
        quizView.getBackButton().addActionListener(e -> {
            viewManagerModel.setState(TIMELINE_PROPERTY);
            viewManagerModel.firePropertyChange();
        });

        setLayout(new BorderLayout(8, 8));
        var header = new JPanel(new BorderLayout());
        header.add(new JLabel("History", SwingConstants.LEFT), BorderLayout.WEST);
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
            JOptionPane.showMessageDialog(this,
                    "Unable to open: content ID is missing.",
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (card.getType() == null) {
            JOptionPane.showMessageDialog(this,
                    "Unable to open: content type is missing.",
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (card.getType()) {
            case "NOTES":
                // Display notes in NotesView
                notesView.displayNotes(card.getContentId(), card.getTitle(), card.getSnippet());
                viewManagerModel.setState(NOTES_VIEW_NAME);
                viewManagerModel.firePropertyChange();
                break;
                
            case "FLASHCARDS":
                // Display flashcards in FlashcardsView
                // Extract number of cards from subtitle (e.g., "20 cards")
                int numCards = 0;
                if (card.getSubtitle() != null && !card.getSubtitle().isEmpty()) {
                    try {
                        String numStr = card.getSubtitle().replaceAll(NON_DIGIT_PATTERN, "");
                        if (!numStr.isEmpty()) {
                            numCards = Integer.parseInt(numStr);
                        }
                    } catch (NumberFormatException e) {
                        // Use default
                    }
                }
                flashcardsView.displayFlashcards(card.getContentId(), numCards);
                viewManagerModel.setState(FLASHCARDS_VIEW_NAME);
                viewManagerModel.firePropertyChange();
                break;
                
            case "QUIZ":
                // Display quiz in QuizView
                // Extract number of questions and score from subtitle
                int numQuestions = 0;
                Double score = null;
                if (card.getSubtitle() != null && !card.getSubtitle().isEmpty()) {
                    if (card.getSubtitle().startsWith("Score")) {
                        // Format: "Score 14.0/15"
                        String[] parts = card.getSubtitle().replace("Score ", "").split("/");
                        if (parts.length == 2) {
                            try {
                                score = Double.parseDouble(parts[0].trim());
                                numQuestions = Integer.parseInt(parts[1].trim());
                            } catch (NumberFormatException e) {
                                // Try to extract just number of questions
                                String numStr = card.getSubtitle().replaceAll(NON_DIGIT_PATTERN, "");
                                if (!numStr.isEmpty()) {
                                    numQuestions = Integer.parseInt(numStr);
                                }
                            }
                        }
                    } else if (card.getSubtitle().contains("questions")) {
                        // Format: "15 questions"
                        String numStr = card.getSubtitle().replaceAll(NON_DIGIT_PATTERN, "");
                        if (!numStr.isEmpty()) {
                            numQuestions = Integer.parseInt(numStr);
                        }
                    }
                }
                quizView.displayQuiz(card.getContentId(), numQuestions, score);
                viewManagerModel.setState(QUIZ_VIEW_NAME);
                viewManagerModel.firePropertyChange();
                break;
                
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown content type: " + card.getType(),
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() { return vm.getViewName(); }
}
