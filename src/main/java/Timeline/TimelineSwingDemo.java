package Timeline;

import interface_adapters.ViewManagerModel;
import views.FlashcardsView;
import views.NotesView;
import views.QuizView;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class TimelineSwingDemo {
    public static void main(String[] args) {
        // 1. Backend: repository and some fake data
        ITimelineRepository repo = new InMemoryTimelineRepository();

        UUID courseId = UUID.randomUUID();

        UUID notesId = UUID.randomUUID();
        TimelineEvent notes = new TimelineEvent(courseId, notesId, TimelineEventType.NOTES_GENERATED);
        notes.setTitle("Week 3 Notes");
        notes.setSnippet("Gradients, level sets, cylindrical coordinates…");
        repo.save(notes);

        UUID cardsId = UUID.randomUUID();
        TimelineEvent cards = new TimelineEvent(courseId, cardsId, TimelineEventType.FLASHCARDS_GENERATED);
        cards.setNumCards(15);
        repo.save(cards);

        UUID quizId = UUID.randomUUID();
        TimelineEvent quizGen = new TimelineEvent(courseId, quizId, TimelineEventType.QUIZ_GENERATED);
        quizGen.setNumQuestions(10);
        repo.save(quizGen);

        TimelineEvent quizSub = new TimelineEvent(courseId, quizId, TimelineEventType.QUIZ_SUBMITTED);
        quizSub.setNumQuestions(10);
        quizSub.setScore(8.0);
        repo.save(quizSub);

        // 2. Use case wiring: VM + presenter + interactor + controller
        ViewTimelineViewModel vm = new ViewTimelineViewModel();
        ViewTimelineSwingPresenter presenter = new ViewTimelineSwingPresenter(vm);
        ViewTimelineInteractor interactor = new ViewTimelineInteractor(repo, presenter);
        TimelineController controller = new TimelineController(interactor);

        // 3. Create views for study materials
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        NotesView notesView = new NotesView();
        FlashcardsView flashcardsView = new FlashcardsView();
        QuizView quizView = new QuizView();

        // 4. The Swing panel
        ViewTimelineView timelineView = new ViewTimelineView(vm, controller, viewManagerModel, notesView, flashcardsView, quizView);

        // 5. Set up card panel for view switching
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.add(timelineView, "timeline");
        cardPanel.add(notesView, "notes");
        cardPanel.add(flashcardsView, "flashcards");
        cardPanel.add(quizView, "quiz");

        // 6. Show window
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Timeline Demo");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(cardPanel, BorderLayout.CENTER);
            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Set initial view to timeline
            viewManagerModel.setState("timeline");
            viewManagerModel.firePropertyChange();

            // Load the timeline for this course
            controller.open(courseId);
        });
    }
}
