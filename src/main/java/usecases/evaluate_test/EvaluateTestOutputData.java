package usecases.evaluate_test;

import entities.EvaluationData;
import java.util.List;

/**
 * Output data for the evaluate test use case.
 */
public class EvaluateTestOutputData {
  private final List<String> questions;
  private final List<String> answers;
  private final List<String> userAnswers;
  private final List<String> correctness;
  private final List<String> feedback;
  private final int score;

  /**
   * Constructs EvaluateTestOutputData from EvaluationData.
   *
   * @param evalData the evaluation data
   */
  public EvaluateTestOutputData(EvaluationData evalData) {
    this.questions = evalData.getQuestions();
    this.answers = evalData.getAnswers();
    this.userAnswers = evalData.getUserAnswers();
    this.correctness = evalData.getCorrectness();
    this.feedback = evalData.getFeedback();
    this.score = evalData.getScore();
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
   * Gets the feedback.
   *
   * @return the feedback
   */
  public List<String> getFeedback() {
    return feedback;
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
