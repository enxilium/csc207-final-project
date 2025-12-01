package entities;

import java.util.List;

/**
 * Data class representing evaluation results for a test.
 */
public class EvaluationData {

  private List<String> questions;
  private List<String> answers;
  private List<String> userAnswers;
  private List<String> correctness;
  private List<String> feedback;
  private int score;

  /**
   * Empty, default constructor for GSON.
   */
  public EvaluationData() {
  }

  /**
   * Constructs EvaluationData with the given parameters.
   *
   * @param questions the questions
   * @param answers the correct answers
   * @param userAnswers the user's answers
   * @param correctness the correctness indicators
   * @param feedback the feedback
   * @param score the score
   */
  public EvaluationData(List<String> questions, List<String> answers,
      List<String> userAnswers, List<String> correctness, List<String> feedback,
      int score) {
    this.questions = questions;
    this.answers = answers;
    this.userAnswers = userAnswers;
    this.correctness = correctness;
    this.feedback = feedback;
    this.score = score;
  }

  /**
   * Gets the questions.
   *
   * @return the questions
   */
  public List<String> getQuestions() {
    return questions;
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
   * Gets the feedback.
   *
   * @return the feedback
   */
  public List<String> getFeedback() {
    return feedback;
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
   * Gets the correctness indicators.
   *
   * @return the correctness indicators
   */
  public List<String> getCorrectness() {
    return correctness;
  }

  /**
   * Gets the score.
   *
   * @return the score
   */
  public int getScore() {
    return score;
  }
}
