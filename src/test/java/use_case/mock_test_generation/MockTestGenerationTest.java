package use_case.mock_test_generation;

import entities.PDFFile;
import entities.TestData;
import org.junit.Before;
import org.junit.Test;
import usecases.mock_test_generation.MockTestGenerationCourseDataAccessInterface;
import usecases.mock_test_generation.MockTestGenerationInputData;
import usecases.mock_test_generation.MockTestGenerationInteractor;
import usecases.mock_test_generation.MockTestGenerationOutputBoundary;
import usecases.mock_test_generation.MockTestGenerationOutputData;
import usecases.mock_test_generation.MockTestGenerationTestDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MockTestGenerationTest {
    private RecordingCourseDAO courseDAO;
    private RecordingTestDAO testDAO;
    private RecordingPresenter presenter;
    private MockTestGenerationInteractor interactor;

    @Before
    public void setUp() {
        courseDAO = new RecordingCourseDAO();
        testDAO = new RecordingTestDAO();
        presenter = new RecordingPresenter();
        interactor = new MockTestGenerationInteractor(courseDAO, testDAO, presenter);
    }

    @Test
    public void execute_buildsChoicesForMultipleChoiceQuestions() {
        List<String> questions = Arrays.asList(
                "What is 2+2?\nA. 1\nB. 2\nC. 4\nD. 3",
                "Explain recursion.");
        List<String> answers = Arrays.asList("C", "");
        List<String> questionTypes = Arrays.asList("Multiple Choice", "Short Answer");
        testDAO.nextData = new TestData(questions, answers, questionTypes);

        MockTestGenerationInputData inputData = new MockTestGenerationInputData("CSC207");
        interactor.execute(inputData);

        assertEquals("CSC207", courseDAO.requestedCourseId);
        assertSame(courseDAO.courseMaterials, testDAO.capturedCourseMaterials);

        assertEquals(1, presenter.loadingCallCount);
        assertNull(presenter.presentedError);
        assertNotNull(presenter.presentedData);

        List<List<String>> choices = presenter.presentedData.getChoices();
        assertEquals(2, choices.size());
        assertEquals(Arrays.asList("1", "2", "4", "3"), choices.get(0));
        assertTrue(choices.get(1).isEmpty());

        assertEquals(choices, testDAO.nextData.getChoices());
        assertEquals(questions, presenter.presentedData.getQuestions());
        assertEquals(answers, presenter.presentedData.getAnswers());
        assertEquals(questionTypes, presenter.presentedData.getQuestionTypes());
    }

    @Test
    public void execute_presentsError_whenTestGenerationThrows() {
        testDAO.failWith = new IOException("generation failed");

        MockTestGenerationInputData inputData = new MockTestGenerationInputData("CSC207");
        interactor.execute(inputData);

        assertEquals("CSC207", courseDAO.requestedCourseId);
        assertEquals(1, presenter.loadingCallCount);
        assertNull(presenter.presentedData);
        assertEquals("generation failed", presenter.presentedError);
    }

    private static class RecordingCourseDAO implements MockTestGenerationCourseDataAccessInterface {
        private final List<PDFFile> courseMaterials = new ArrayList<>(
                List.of(new PDFFile("/tmp/mock-material.pdf")));
        private String requestedCourseId;

        @Override
        public List<PDFFile> getCourseMaterials(String courseId) {
            this.requestedCourseId = courseId;
            return courseMaterials;
        }
    }

    private static class RecordingTestDAO implements MockTestGenerationTestDataAccessInterface {
        private List<PDFFile> capturedCourseMaterials;
        private TestData nextData;
        private IOException failWith;

        @Override
        public TestData getTestData(List<PDFFile> courseMaterials) throws IOException {
            if (failWith != null) {
                throw failWith;
            }

            this.capturedCourseMaterials = courseMaterials;
            return nextData;
        }
    }

    private static class RecordingPresenter implements MockTestGenerationOutputBoundary {
        private int loadingCallCount;
        private MockTestGenerationOutputData presentedData;
        private String presentedError;

        @Override
        public void presentTest(MockTestGenerationOutputData mockTestGenerationOutputData) {
            this.presentedData = mockTestGenerationOutputData;
        }

        @Override
        public void presentLoading() {
            loadingCallCount++;
        }

        @Override
        public void presentError(String errorMessage) {
            this.presentedError = errorMessage;
        }
    }
}
