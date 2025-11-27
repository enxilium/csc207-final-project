package usecases.evaluate_test;

import entities.EvaluationData;
import entities.PDFFile;

import java.util.List;

public class EvaluateTestInteractor implements EvaluateTestInputBoundary {
    private final EvaluateTestCourseDataAccessInterface evalTestGenerationCourseDAO;
    private final EvaluateTestOutputBoundary evalTestGenerationPresenter;
    private final EvaluateTestDataAccessInterface evalTestGenerationDAO;

    public EvaluateTestInteractor(EvaluateTestCourseDataAccessInterface evalTestCourseDAO,
                                  EvaluateTestDataAccessInterface evalTestDAO,
                                  EvaluateTestOutputBoundary evalTestPresenter) {
        this.evalTestGenerationPresenter = evalTestPresenter;
        this.evalTestGenerationCourseDAO = evalTestCourseDAO;
        this.evalTestGenerationDAO = evalTestDAO;
    }

    public void execute(EvaluateTestInputData evaluateTestInputData) {
        try {
            evalTestGenerationPresenter.presentLoading();

            String courseId = evaluateTestInputData.getCourseID();
            List<String> userAnswers = evaluateTestInputData.getUserAnswers();
            List<String> questions = evaluateTestInputData.getQuestions();
            List<String> answers = evaluateTestInputData.getAnswers();

            List<PDFFile> courseMaterials = evalTestGenerationCourseDAO.getCourseMaterials(courseId);

            EvaluationData evalData = evalTestGenerationDAO.getEvaluationResults(courseMaterials, userAnswers, questions, answers);

            EvaluateTestOutputData evaluateTestOutputData = new EvaluateTestOutputData(evalData);

            evalTestGenerationPresenter.presentEvaluationResults(evaluateTestOutputData);
        } catch (Exception e) {
            evalTestGenerationPresenter.presentError(e.getMessage());
        }
    }
}
