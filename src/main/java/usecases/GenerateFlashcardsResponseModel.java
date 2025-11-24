package usecases;

import entities.FlashcardSet;

/**
 * Response model that wraps the generated flashcard set
 * before sending it to the presenter.
 */
public class GenerateFlashcardsResponseModel {
    private final FlashcardSet flashcardSet;

    public GenerateFlashcardsResponseModel(FlashcardSet flashcardSet) {
        this.flashcardSet = flashcardSet;
    }

    public FlashcardSet getFlashcardSet() {
        return flashcardSet;
    }
}