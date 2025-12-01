package usecases.evaluate_test;

import entities.PDFFile;
import java.util.List;

/**
 * Interface for accessing course materials for test evaluation.
 */
public interface EvaluateTestCourseDataAccessInterface {
  /**
   * Gets the course materials for a given course ID.
   *
   * @param courseId the course ID
   * @return the list of PDF files for the course
   */
  List<PDFFile> getCourseMaterials(String courseId);
}
