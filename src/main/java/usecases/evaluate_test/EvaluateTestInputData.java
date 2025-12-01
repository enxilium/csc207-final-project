package usecases.evaluate_test;

import java.util.List;

/**
 * Input data for the evaluate test use case.
 */
public class EvaluateTestInputData {
  private final String courseId;
  private final List<String> userAnswers;
  private final List<String> questions;
  private final List<String> answers;

  /**
   * Constructs EvaluateTestInputData with the given parameters.
   *
   * @param courseId the course ID
   * @param userAnswers the user's answers
   * @param questions the questions
   * @param answers the correct answers
   */
  public EvaluateTestInputData(String courseId, List<String> userAnswers,
      List<String> questions, List<String> answers) {
    this.courseId = courseId;
    this.userAnswers = userAnswers;
    this.questions = questions;
    this.answers = answers;
  }

  /**
   * Gets the course ID.
   *
   * @return the course ID
   */
  public String getCourseId() {
    return courseId;
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
}
