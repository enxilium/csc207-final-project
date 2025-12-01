package entities;

import java.util.List;

/**
 * Represents a set of flashcards for a course.
 */
public class FlashcardSet {
  private final String courseName;
  private final List<Flashcard> flashcards;

  /**
   * Constructs a FlashcardSet with the given course name and flashcards.
   *
   * @param courseName the name of the course
   * @param flashcards the list of flashcards
   */
  public FlashcardSet(String courseName, List<Flashcard> flashcards) {
    this.courseName = courseName;
    this.flashcards = flashcards;
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
   * Gets the list of flashcards.
   *
   * @return the list of flashcards
   */
  public List<Flashcard> getFlashcards() {
    return flashcards;
  }

  /**
   * Gets the number of flashcards in this set.
   *
   * @return the number of flashcards
   */
  public int size() {
    return flashcards.size();
  }

  /**
   * Gets the flashcard at the specified index.
   *
   * @param index the index
   * @return the flashcard at the index
   */
  public Flashcard getCard(int index) {
    return flashcards.get(index);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Flashcard Set for Course: ").append(courseName).append("\n");
    for (Flashcard f : flashcards) {
      sb.append(" - ").append(f.toString()).append("\n");
    }
    return sb.toString();
  }
}
