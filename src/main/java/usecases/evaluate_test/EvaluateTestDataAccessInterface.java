package usecases.evaluate_test;

import entities.EvaluationData;
import entities.PDFFile;
import java.io.IOException;
import java.util.List;

/**
 * Interface for accessing evaluation data.
 */
public interface EvaluateTestDataAccessInterface {
  /**
   * Gets evaluation results for the given test data.
   *
   * @param courseMaterials the course materials
   * @param userAnswers the user's answers
   * @param questions the questions
   * @param answers the correct answers
   * @return the evaluation data
   * @throws IOException if an I/O error occurs
   */
  EvaluationData getEvaluationResults(List<PDFFile> courseMaterials,
      List<String> userAnswers, List<String> questions, List<String> answers)
      throws IOException;
}
