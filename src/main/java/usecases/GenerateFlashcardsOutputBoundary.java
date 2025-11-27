package usecases;

/**
 * Output boundary interface for the flashcard generation use case.
 * Defines how results and errors are presented to the user or console.
 */
public interface GenerateFlashcardsOutputBoundary {
    void presentFlashcards(GenerateFlashcardsResponseModel responseModel);
    void presentError(String message);
}