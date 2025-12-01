package usecases;

/**
 * Output boundary interface for the flashcard generation use case.
 * Defines how results and errors are presented to the user or console.
 */
public interface GenerateFlashcardsOutputBoundary {
  /**
   * Presents the generated flashcards to the user.
   *
   * @param responseModel the response model containing the flashcards
   */
  void presentFlashcards(GenerateFlashcardsResponseModel responseModel);

  /**
   * Presents an error message to the user.
   *
   * @param message the error message
   */
  void presentError(String message);
}
