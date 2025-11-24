package interface_adapters.flashcards;

import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsResponseModel;

/**
 * Presenter for flashcard generation.
 * Updates the ViewModel with generated flashcards or error messages.
 */
public class GenerateFlashcardsPresenter implements GenerateFlashcardsOutputBoundary {
    private final FlashcardViewModel viewModel;

    public GenerateFlashcardsPresenter(FlashcardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentFlashcards(GenerateFlashcardsResponseModel responseModel) {
        viewModel.setLoading(false);
        viewModel.setCurrentFlashcardSet(responseModel.getFlashcardSet());
        viewModel.setErrorMessage(null);
    }

    @Override
    public void presentError(String message) {
        viewModel.setLoading(false);
        viewModel.setErrorMessage(message);
    }
}