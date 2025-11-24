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
    private final ViewTimelineViewModel vm;
    private final TimelineController controller;
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
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;
        this.notesView = notesView;
        this.flashcardsView = flashcardsView;
        this.quizView = quizView;
        this.vm.addPropertyChangeListener(this);
        
        // Set up back buttons to return to timeline
        notesView.getBackButton().addActionListener(_ -> {
            viewManagerModel.setState("timeline");
            viewManagerModel.firePropertyChange();
        });
        flashcardsView.getBackButton().addActionListener(_ -> {
            viewManagerModel.setState("timeline");
            viewManagerModel.firePropertyChange();
        });
        quizView.getBackButton().addActionListener(_ -> {
            viewManagerModel.setState("timeline");
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
                    if (card.contentId == null) {
                        // Try to get from viewModel if missing
                        List<ViewTimelineResponse.TimelineCardVM> items = vm.getItems();
                        if (index < items.size()) {
                            card = items.get(index);
                        }
                    }
                    // Open on double-click or single-click
                    if (e.getClickCount() >= 1 && card.contentId != null) {
                        openStudyMaterial(card);
                    }
                }
            }
        });

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);

        emptyLabel.setVisible(false);

        refreshBtn.addActionListener(_ -> {
            if (vm.getCourseId() != null) controller.open(vm.getCourseId());
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"timeline".equals(evt.getPropertyName())) return;

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

            title.setText(value.title != null ? value.title : value.type);
            String sub = (value.subtitle == null || value.subtitle.isEmpty())
                    ? (value.snippet == null ? "" : value.snippet)
                    : value.subtitle;
            subtitle.setText(sub);
            time.setText(value.time != null ? value.time : "");

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
        if (card.contentId == null) {
            JOptionPane.showMessageDialog(this,
                    "Unable to open: content ID is missing.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (card.type == null) {
            JOptionPane.showMessageDialog(this,
                    "Unable to open: content type is missing.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (card.type) {
            case "NOTES":
                // Display notes in NotesView
                notesView.displayNotes(card.contentId, card.title, card.snippet);
                viewManagerModel.setState("notes");
                viewManagerModel.firePropertyChange();
                break;
                
            case "FLASHCARDS":
                // Display flashcards in FlashcardsView
                // Extract number of cards from subtitle (e.g., "20 cards")
                int numCards = 0;
                if (card.subtitle != null && !card.subtitle.isEmpty()) {
                    try {
                        String numStr = card.subtitle.replaceAll("[^0-9]", "");
                        if (!numStr.isEmpty()) {
                            numCards = Integer.parseInt(numStr);
                        }
                    } catch (NumberFormatException e) {
                        // Use default
                    }
                }
                flashcardsView.displayFlashcards(card.contentId, numCards);
                viewManagerModel.setState("flashcards");
                viewManagerModel.firePropertyChange();
                break;
                
            case "QUIZ":
                // Display quiz in QuizView
                // Extract number of questions and score from subtitle
                int numQuestions = 0;
                Double score = null;
                if (card.subtitle != null && !card.subtitle.isEmpty()) {
                    if (card.subtitle.startsWith("Score")) {
                        // Format: "Score 14.0/15"
                        String[] parts = card.subtitle.replace("Score ", "").split("/");
                        if (parts.length == 2) {
                            try {
                                score = Double.parseDouble(parts[0].trim());
                                numQuestions = Integer.parseInt(parts[1].trim());
                            } catch (NumberFormatException e) {
                                // Try to extract just number of questions
                                String numStr = card.subtitle.replaceAll("[^0-9]", "");
                                if (!numStr.isEmpty()) {
                                    numQuestions = Integer.parseInt(numStr);
                                }
                            }
                        }
                    } else if (card.subtitle.contains("questions")) {
                        // Format: "15 questions"
                        String numStr = card.subtitle.replaceAll("[^0-9]", "");
                        if (!numStr.isEmpty()) {
                            numQuestions = Integer.parseInt(numStr);
                        }
                    }
                }
                quizView.displayQuiz(card.contentId, numQuestions, score);
                viewManagerModel.setState("quiz");
                viewManagerModel.firePropertyChange();
                break;
                
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown content type: " + card.type,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() { return vm.getViewName(); }
}
