package usecases;

/**
 * Request model for flashcard generation.
 */
public class GenerateFlashcardsRequestModel {
  private final String courseName;
  private final String content;

  /**
   * Constructs a GenerateFlashcardsRequestModel with the given course name
   * and content.
   *
   * @param courseName the name of the course
   * @param content the content to generate flashcards from
   */
  public GenerateFlashcardsRequestModel(String courseName, String content) {
    this.courseName = courseName;
    this.content = content;
  }

  /**
   * Gets the course name.
   *
   * @return the course name
   */
  public String getCourseName() {
    return courseName;
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }
}
