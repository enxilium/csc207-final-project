package usecases;

/**
 * Input boundary interface for the flashcard generation use case.
 * Defines the method that triggers the generation process.
 */
public interface GenerateFlashcardsInputBoundary {
    void execute(String courseName, String content);
}