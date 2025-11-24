package interface_adapters.flashcards;

import interface_adapters.ViewManagerModel;
import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsResponseModel;

import javax.swing.SwingUtilities;

/**
 * Presenter for flashcard generation.
 * Updates the ViewModel with generated flashcards or error messages.
 */
public class GenerateFlashcardsPresenter implements GenerateFlashcardsOutputBoundary {
    private final FlashcardViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public GenerateFlashcardsPresenter(FlashcardViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
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