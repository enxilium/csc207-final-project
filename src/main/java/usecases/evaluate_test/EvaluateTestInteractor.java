package usecases.evaluate_test;

import entities.EvaluationData;
import entities.PDFFile;
import java.util.List;

/**
 * Interactor for the evaluate test use case.
 */
public class EvaluateTestInteractor implements EvaluateTestInputBoundary {
  private final EvaluateTestCourseDataAccessInterface evalTestGenerationCourseDao;
  private final EvaluateTestOutputBoundary evalTestGenerationPresenter;
  private final EvaluateTestDataAccessInterface evalTestGenerationDao;

  /**
   * Constructs an EvaluateTestInteractor with the given dependencies.
   *
   * @param evalTestCourseDao the data access for course materials
   * @param evalTestDao the data access for evaluation results
   * @param evalTestPresenter the presenter for displaying results
   */
  public EvaluateTestInteractor(EvaluateTestCourseDataAccessInterface evalTestCourseDao,
      EvaluateTestDataAccessInterface evalTestDao,
      EvaluateTestOutputBoundary evalTestPresenter) {
    this.evalTestGenerationPresenter = evalTestPresenter;
    this.evalTestGenerationCourseDao = evalTestCourseDao;
    this.evalTestGenerationDao = evalTestDao;
  }

  @Override
  public void execute(EvaluateTestInputData evaluateTestInputData) {
    try {
      evalTestGenerationPresenter.presentLoading();

      String courseId = evaluateTestInputData.getCourseId();
      List<String> userAnswers = evaluateTestInputData.getUserAnswers();
      List<String> questions = evaluateTestInputData.getQuestions();
      List<String> answers = evaluateTestInputData.getAnswers();

      List<PDFFile> courseMaterials =
          evalTestGenerationCourseDao.getCourseMaterials(courseId);

      EvaluationData evalData = evalTestGenerationDao.getEvaluationResults(
          courseMaterials, userAnswers, questions, answers);

      EvaluateTestOutputData evaluateTestOutputData =
          new EvaluateTestOutputData(evalData);

      evalTestGenerationPresenter.presentEvaluationResults(evaluateTestOutputData);
    } catch (Exception e) {
      evalTestGenerationPresenter.presentError(e.getMessage());
    }
  }
}
