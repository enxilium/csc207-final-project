package app;

import Timeline.*;
import interface_adapters.ViewManagerModel;
import views.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    // --- Shared Components held by the Builder ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    // ========== TIMELINE FEATURE START ==========
    // Timeline components - add these fields to your existing AppBuilder
    private final ITimelineRepository timelineRepository;
    private final TimelineLogger timelineLogger;
    // ========== TIMELINE FEATURE END ==========
    
    private String initialViewName; // Track the first view added for initial state

    public AppBuilder() {
        // Initialize ViewManager (registers itself as listener, doesn't need to be stored)
        new ViewManager(cardPanel, cardLayout, viewManagerModel);
        
        // ========== TIMELINE FEATURE START ==========
        // Initialize Timeline repository - add this to your existing constructor
        timelineRepository = new InMemoryTimelineRepository();
        timelineLogger = new TimelineLogger(timelineRepository);
        // ========== TIMELINE FEATURE END ==========
    }

    // ========== TIMELINE FEATURE START ==========
    // Add this method to your existing AppBuilder class

    /**
     * Adds the Timeline view to the application.
     * This method can be called alongside other feature setup methods.
     * @return this AppBuilder instance for method chaining
     */
    public AppBuilder addTimelineView() {
        // Create study material views for displaying content when timeline items are clicked
        NotesView notesView = new NotesView();
        FlashcardsView flashcardsView = new FlashcardsView();
        QuizView quizView = new QuizView();

        // Wire up Timeline components
        ViewTimelineViewModel timelineViewModel = new ViewTimelineViewModel();
        ViewTimelineSwingPresenter presenter = new ViewTimelineSwingPresenter(timelineViewModel);
        ViewTimelineInteractor interactor = new ViewTimelineInteractor(timelineRepository, presenter);
        TimelineController timelineController = new TimelineController(interactor);

        // Create the Timeline view
        ViewTimelineView timelineView = new ViewTimelineView(timelineViewModel, timelineController,
                                          viewManagerModel, notesView, flashcardsView, quizView);

        // Register all views with the card panel
        cardPanel.add(timelineView, timelineViewModel.getViewName());
        cardPanel.add(notesView, "notes");
        cardPanel.add(flashcardsView, "flashcards");
        cardPanel.add(quizView, "quiz");

        // Set as initial view if this is the first view added
        if (initialViewName == null) {
            initialViewName = timelineViewModel.getViewName();
        }

        return this;
    }

    /**
     * Gets the TimelineLogger instance for other features to log Timeline events.
     * Other features can use this to log when they generate notes, flashcards, or quizzes.
     * @return The TimelineLogger instance
     */
    public TimelineLogger getTimelineLogger() {
        return timelineLogger;
    }
    // ========== TIMELINE FEATURE END ==========

    /**
     * Sets the initial view name. Can be called to override the default (first added view).
     * @param viewName The name of the view to show initially
     * @return this AppBuilder instance for method chaining
     */
    public AppBuilder setInitialView(String viewName) {
        this.initialViewName = viewName;
        return this;
    }

    /**
     * Builds and returns the main application JFrame.
     * @return The configured JFrame
     */
    public JFrame build() {
        JFrame application = new JFrame("StudyFlow AI Assistant");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        // Set the initial view (use the first added view if no specific initial view was set)
        if (initialViewName != null) {
            viewManagerModel.setState(initialViewName);
            viewManagerModel.firePropertyChange();
        }

        return application;
    }
}
