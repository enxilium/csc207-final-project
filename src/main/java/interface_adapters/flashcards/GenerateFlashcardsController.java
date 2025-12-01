package interface_adapters.flashcards;

import usecases.GenerateFlashcardsInputBoundary;

/**
 * Controller for flashcard generation.
 * Receives user input and delegates to the use case interactor.
 */
public class GenerateFlashcardsController {
  private final GenerateFlashcardsInputBoundary interactor;

  /**
   * Constructs a GenerateFlashcardsController with the given interactor.
   *
   * @param interactor the flashcard generation interactor
   */
  public GenerateFlashcardsController(GenerateFlashcardsInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Triggers flashcard generation for a given course and topic/content.
   *
   * @param courseName The name of the course
   * @param content The topic or PDF path to generate flashcards from
   */
  public void generateFlashcards(String courseName, String content) {
    interactor.execute(courseName, content);
  }
}
