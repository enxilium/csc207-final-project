package usecases.evaluate_test;

import entities.EvaluationData;
import entities.PDFFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class EvaluateTestInteractorTest {

    private EvaluateTestInteractor interactor;
    private RecordingPresenter presenter;
    private EvaluateTestCourseDataAccessInterface courseDao;
    private EvaluateTestDataAccessInterface evalDao;

    @BeforeEach
    void setUp() {
        presenter = new RecordingPresenter();
        courseDao = courseId -> Collections.singletonList(new PDFFile("dummy.pdf"));
    }

    @Test
    void execute_delegatesToGatewaysAndPresentsResults() throws IOException {
        EvaluationData data = new EvaluationData(
                Arrays.asList("Q1"),
                Arrays.asList("A1"),
                Arrays.asList("UA1"),
                Arrays.asList("1"),
                Arrays.asList("Good job"),
                100
        );

        evalDao = (materials, userAnswers, questions, answers) -> data;

        interactor = new EvaluateTestInteractor(courseDao, evalDao, presenter);

        EvaluateTestInputData input = new EvaluateTestInputData(
                "CSC207",
                Collections.singletonList("UA1"),
                Collections.singletonList("Q1"),
                Collections.singletonList("A1")
        );

        interactor.execute(input);

        Assertions.assertTrue(presenter.loadingCalled, "Presenter should show loading state early");
        Assertions.assertNotNull(presenter.lastOutput, "Presenter should receive evaluation data");
        Assertions.assertTrue(presenter.errors.isEmpty(), "No errors expected for successful run");
        Assertions.assertEquals(100, presenter.lastOutput.getScore());
    }

    @Test
    void execute_reportsErrorsFromEvaluationGateway() throws IOException {
        evalDao = (materials, userAnswers, questions, answers) -> {
            throw new IOException("Evaluation failed");
        };

        interactor = new EvaluateTestInteractor(courseDao, evalDao, presenter);

        EvaluateTestInputData input = new EvaluateTestInputData(
                "CSC207",
                Collections.singletonList("UA1"),
                Collections.singletonList("Q1"),
                Collections.singletonList("A1")
        );

        interactor.execute(input);

        Assertions.assertTrue(presenter.loadingCalled, "Loading should be invoked even when errors occur");
        Assertions.assertNull(presenter.lastOutput, "No evaluation results should be presented after failure");
        Assertions.assertEquals(Collections.singletonList("Evaluation failed"), presenter.errors);
    }

    private static class RecordingPresenter implements EvaluateTestOutputBoundary {
        private boolean loadingCalled = false;
        private EvaluateTestOutputData lastOutput = null;
        private final List<String> errors = new java.util.ArrayList<>();

        @Override
        public void presentEvaluationResults(EvaluateTestOutputData evaluateTestOutputData) {
            this.lastOutput = evaluateTestOutputData;
        }

        @Override
        public void presentLoading() {
            this.loadingCalled = true;
        }

        @Override
        public void presentError(String errorMessage) {
            this.errors.add(errorMessage);
        }
    }
}