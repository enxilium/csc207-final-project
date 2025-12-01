package usecases.mock_test_generation;

import entities.PDFFile;
import entities.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MockTestGenerationInteractorTest {

    private RecordingPresenter presenter;
    private MockTestGenerationCourseDataAccessInterface courseDao;
    private MockTestGenerationTestDataAccessInterface testDao;

    @BeforeEach
    void setUp() {
        presenter = new RecordingPresenter();
        courseDao = courseId -> Collections.singletonList(new PDFFile("dummy.pdf"));
    }

    @Test
    void execute_buildsChoiceMatrixForMultipleChoiceQuestions() throws IOException {
        List<String> questions = Arrays.asList(
                "What is 2 + 2?\nA. Three\nB. Four\nC. Five\nD. Six",
                "Explain the significance of algorithms."
        );
        List<String> answers = Arrays.asList("B", "");
        List<String> questionTypes = Arrays.asList(" Multiple Choice ", "Essay");
        TestData testData = new TestData(questions, answers, questionTypes);

        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        Assertions.assertTrue(presenter.loadingCalled, "Presenter should show the loading view first");
        Assertions.assertNotNull(presenter.lastOutput, "Presenter should receive generated data");
        Assertions.assertTrue(presenter.errors.isEmpty(), "No errors expected during successful generation");

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(2, choices.size(), "Choices list should align with question count");
        Assertions.assertEquals(Arrays.asList("Three", "Four", "Five", "Six"), choices.get(0));
        Assertions.assertEquals(Collections.emptyList(), choices.get(1));
    }

    @Test
    void execute_handlesNullAndUnstructuredChoicesGracefully() throws IOException {
        List<String> questions = new ArrayList<>();
        questions.add(null);
        questions.add("Which statement is correct?\nOption 1\nOption 2");

        List<String> answers = Arrays.asList("A", "");
        List<String> questionTypes = Arrays.asList("Multiple Choice", "Multiple Choice");
        TestData testData = new TestData(questions, answers, questionTypes);

        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(2, choices.size());
        Assertions.assertEquals(Collections.emptyList(), choices.get(0),
                "Null questions should yield empty options");
        Assertions.assertEquals(Collections.emptyList(), choices.get(1),
                "Questions without labelled options should still yield empty lists");
    }

    @Test
    void execute_returnsEmptyChoicesWhenQuestionsMissing() throws IOException {
        TestData testData = new TestData(null, null, null);
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        Assertions.assertEquals(Collections.emptyList(), presenter.lastOutput.getChoices());
        Assertions.assertTrue(presenter.errors.isEmpty());
    }

    @Test
    void execute_reportsErrorsFromGateway() throws IOException {
        testDao = courseMaterials -> { throw new IOException("Test data unavailable"); };

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        Assertions.assertTrue(presenter.loadingCalled, "Loading state should still be triggered");
        Assertions.assertTrue(presenter.lastOutput == null || presenter.lastOutput.getChoices().isEmpty());
        Assertions.assertEquals(Collections.singletonList("Test data unavailable"), presenter.errors);
    }

    @Test
    void execute_treatsMissingQuestionTypeEntriesAsNonMultipleChoice() throws IOException {
        List<String> questions = Arrays.asList(
                "Select all that apply:\nA. Option 1\nB. Option 2",
                "Explain the purpose of unit testing."
        );

        List<String> answers = Arrays.asList("A", "");
        // Second question lacks a corresponding type entry to trigger the false branch in the guard.
        List<String> questionTypes = Collections.singletonList("Multiple Choice");

        TestData testData = new TestData(questions, answers, questionTypes);
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(2, choices.size());
        Assertions.assertEquals(Arrays.asList("Option 1", "Option 2"), choices.get(0));
        Assertions.assertEquals(Collections.emptyList(), choices.get(1),
                "Missing question type metadata should default to non-multiple-choice");
    }

    @Test
    void execute_handlesExplicitNullQuestionTypeEntries() throws IOException {
        List<String> questions = Arrays.asList(
                "Which letter comes first?\nA. A\nB. B",
                "Describe polymorphism."
        );

        List<String> answers = Arrays.asList("A", "");
        List<String> questionTypes = Arrays.asList(null, "Essay");

        TestData testData = new TestData(questions, answers, questionTypes);
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(2, choices.size());
        Assertions.assertEquals(Collections.emptyList(), choices.get(0),
                "Null question type entries should be treated as non-multiple-choice");
        Assertions.assertEquals(Collections.emptyList(), choices.get(1));
    }

    @Test
    void execute_handlesNullQuestionTypesArray() throws IOException {
        List<String> questions = Collections.singletonList(
                "Pick the correct answer:\nA. True\nB. False");

        List<String> answers = Collections.singletonList("A");
        TestData testData = new TestData(questions, answers, null);
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(1, choices.size());
        Assertions.assertEquals(Collections.emptyList(), choices.get(0),
                "Null question type list should default to empty choice list");
    }

    @Test
    void execute_handlesEmptyQuestionsList() throws IOException {
        TestData testData = new TestData(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        Assertions.assertEquals(Collections.emptyList(), presenter.lastOutput.getChoices(),
                "Empty question lists should return empty choices");
        Assertions.assertTrue(presenter.errors.isEmpty());
    }

    @Test
    void execute_handlesEmptyQuestionText() throws IOException {
        List<String> questions = Arrays.asList("", "What is encapsulation?");
        List<String> answers = Arrays.asList("A", "");
        List<String> questionTypes = Arrays.asList("Multiple Choice", "Essay");

        TestData testData = new TestData(questions, answers, questionTypes);
        testDao = courseMaterials -> testData;

        MockTestGenerationInteractor interactor =
                new MockTestGenerationInteractor(courseDao, testDao, presenter);

        interactor.execute(new MockTestGenerationInputData("CSC207"));

        List<List<String>> choices = presenter.lastOutput.getChoices();
        Assertions.assertEquals(2, choices.size());
        Assertions.assertEquals(Collections.emptyList(), choices.get(0),
                "Empty question text should yield no multiple-choice options");
        Assertions.assertEquals(Collections.emptyList(), choices.get(1));
    }

    private static class RecordingPresenter implements MockTestGenerationOutputBoundary {
        private boolean loadingCalled = false;
        private MockTestGenerationOutputData lastOutput = null;
        private final List<String> errors = new ArrayList<>();

        @Override
        public void presentTest(MockTestGenerationOutputData mockTestGenerationOutputData) {
            this.lastOutput = mockTestGenerationOutputData;
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