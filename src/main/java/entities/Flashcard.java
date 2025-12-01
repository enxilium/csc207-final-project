package entities;

/**
 * Represents a flashcard with a question and answer.
 */
public class Flashcard {
  private final String question;
  private final String answer;

  // Optional new fields for front/back (synonyms for question/answer)
  private final String front;
  private final String back;

  /**
   * Constructs a Flashcard with the given question and answer.
   *
   * @param question the question text
   * @param answer the answer text
   */
  public Flashcard(String question, String answer) {
    this.question = question;
    this.answer = answer;
    this.front = question;
    this.back = answer;
  }

  /**
   * Gets the question text.
   *
   * @return the question text
   */
  public String getQuestion() {
    return question;
  }

  /**
   * Gets the answer text.
   *
   * @return the answer text
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * Gets the front text (synonym for question).
   *
   * @return the front text
   */
  public String getFront() {
    return front;
  }

  /**
   * Gets the back text (synonym for answer).
   *
   * @return the back text
   */
  public String getBack() {
    return back;
  }

  @Override
  public String toString() {
    return "Q: " + question + " | A: " + answer;
  }
}
