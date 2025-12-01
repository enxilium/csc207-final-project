package data_access;

import entities.FlashcardSet;
import java.io.IOException;

/**
 * Interface for generating flashcards from course content.
 */
public interface FlashcardGenerator {
  /**
   * Generates flashcards for a course from the given content.
   *
   * @param courseName the name of the course
   * @param content the content to generate flashcards from
   * @return a FlashcardSet containing the generated flashcards
   * @throws IOException if an I/O error occurs
   */
  FlashcardSet generateForCourse(String courseName, String content) throws IOException;
}
