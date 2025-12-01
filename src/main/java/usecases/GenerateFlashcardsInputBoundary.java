package usecases;

/**
 * Input boundary interface for the flashcard generation use case.
 * Defines the method that triggers the generation process.
 */
public interface GenerateFlashcardsInputBoundary {
  /**
   * Executes the flashcard generation process.
   *
   * @param courseName the name of the course
   * @param content the content to generate flashcards from
   */
  void execute(String courseName, String content);
}
