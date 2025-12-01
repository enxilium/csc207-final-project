package usecases;

import entities.FlashcardSet;

/**
 * Response model that wraps the generated flashcard set
 * before sending it to the presenter.
 */
public class GenerateFlashcardsResponseModel {
  private final FlashcardSet flashcardSet;

  /**
   * Constructs a GenerateFlashcardsResponseModel with the given flashcard set.
   *
   * @param flashcardSet the flashcard set to wrap
   */
  public GenerateFlashcardsResponseModel(FlashcardSet flashcardSet) {
    this.flashcardSet = flashcardSet;
  }

  /**
   * Gets the flashcard set.
   *
   * @return the flashcard set
   */
  public FlashcardSet getFlashcardSet() {
    return flashcardSet;
  }
}
