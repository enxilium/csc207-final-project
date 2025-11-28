package interface_adapters.flashcards;

import Timeline.CourseIdMapper;
import Timeline.TimelineLogger;
import interface_adapters.ViewManagerModel;
import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsResponseModel;

import javax.swing.SwingUtilities;
import java.util.UUID;

/**
 * Presenter for flashcard generation.
 * Updates the ViewModel with generated flashcards or error messages.
 */
public class GenerateFlashcardsPresenter implements GenerateFlashcardsOutputBoundary {
    private final FlashcardViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final TimelineLogger timelineLogger;

    public GenerateFlashcardsPresenter(FlashcardViewModel viewModel, ViewManagerModel viewManagerModel, TimelineLogger timelineLogger) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.timelineLogger = timelineLogger;
    }

    @Override
    public void presentFlashcards(GenerateFlashcardsResponseModel responseModel) {
        // Run on EDT to ensure UI updates correctly
        SwingUtilities.invokeLater(() -> {
            viewModel.setLoading(false);
            viewModel.setCurrentFlashcardSet(responseModel.getFlashcardSet());
            viewModel.setErrorMessage(null);

            // Switch to FlashcardDisplayView
            viewManagerModel.setState("flashcardDisplay");
            viewManagerModel.firePropertyChange();

            // Log to Timeline
            if (timelineLogger != null && responseModel.getFlashcardSet() != null) {
                try {
                    String courseName = responseModel.getFlashcardSet().getCourseName();
                    if (courseName != null && !courseName.isEmpty()) {
                        UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseName);
                        UUID contentId = UUID.randomUUID(); // Generate a unique content ID for these flashcards
                        int numCards = responseModel.getFlashcardSet().getFlashcards() != null 
                            ? responseModel.getFlashcardSet().getFlashcards().size() : 0;
                        timelineLogger.logFlashcardsGenerated(courseUuid, contentId, numCards, responseModel.getFlashcardSet());
                    }
                } catch (Exception e) {
                    // Log error but don't break the flow
                    System.err.println("Failed to log flashcards to timeline: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void presentError(String message) {
        SwingUtilities.invokeLater(() -> {
            viewModel.setLoading(false);
            viewModel.setErrorMessage(message);
        });
    }
}