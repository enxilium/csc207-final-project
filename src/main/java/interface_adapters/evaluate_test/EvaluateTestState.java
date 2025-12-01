package interface_adapters.evaluate_test;

import java.util.ArrayList;
import java.util.List;

/**
 * State class for the evaluate test view model.
 */
public class EvaluateTestState {
  // The full test data
  private List<String> questions = new ArrayList<>();
  // The correct answers
  private List<String> answers = new ArrayList<>();
  // -1 means evaluation is not active
  private int currentQuestionIndex = 0;
  // user provided answers
  private List<String> userAnswers;
  // integer indicating whether the user answer was correct or not.
  private List<String> correctness;
  // String feedback if necessary
  private List<String> feedback;
  private int score;

  /**
   * Gets the questions.
   *
   * @return the questions
   */
  public List<String> getQuestions() {
    return questions;
  }

  /**
   * Sets the questions.
   *
   * @param questions the questions to set
   */
  public void setQuestions(List<String> questions) {
    this.questions = questions;
  }

  /**
   * Gets the correct answers.
   *
   * @return the correct answers
   */
  public List<String> getAnswers() {
    return answers;
  }

  /**
   * Sets the correct answers.
   *
   * @param answers the answers to set
   */
  public void setAnswers(List<String> answers) {
    this.answers = answers;
  }

  /**
   * Gets the current question index.
   *
   * @return the current question index
   */
  public int getCurrentQuestionIndex() {
    return currentQuestionIndex;
  }

  /**
   * Sets the current question index.
   *
   * @param currentQuestionIndex the index to set
   */
  public void setCurrentQuestionIndex(int currentQuestionIndex) {
    this.currentQuestionIndex = currentQuestionIndex;
  }

  /**
   * Gets the user's answers.
   *
   * @return the user's answers
   */
  public List<String> getUserAnswers() {
    return userAnswers;
  }

  /**
   * Sets the user's answers.
   *
   * @param userAnswers the user's answers to set
   */
  public void setUserAnswers(List<String> userAnswers) {
    this.userAnswers = userAnswers;
  }

  /**
   * Gets the correctness indicators.
   *
   * @return the correctness indicators
   */
  public List<String> getCorrectness() {
    return correctness;
  }

  /**
   * Sets the correctness indicators.
   *
   * @param correctness the correctness indicators to set
   */
  public void setCorrectness(List<String> correctness) {
    this.correctness = correctness;
  }

  /**
   * Gets the score.
   *
   * @return the score
   */
  public int getScore() {
    return score;
  }

  /**
   * Sets the score.
   *
   * @param score the score to set
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Gets the feedback.
   *
   * @return the feedback
   */
  public List<String> getFeedback() {
    return feedback;
  }

  /**
   * Sets the feedback.
   *
   * @param feedback the feedback to set
   */
  public void setFeedback(List<String> feedback) {
    this.feedback = feedback;
  }
}
