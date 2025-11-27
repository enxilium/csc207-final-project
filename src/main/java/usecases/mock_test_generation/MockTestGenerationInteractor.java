package usecases.mock_test_generation;

import entities.PDFFile;
import entities.TestData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockTestGenerationInteractor implements MockTestGenerationInputBoundary {
    private static final Pattern MULTIPLE_CHOICE_OPTION_PATTERN =
            Pattern.compile("(?m)^[A-D][\\).]\\s*(.+)");
    private final MockTestGenerationCourseDataAccessInterface mockTestGenerationCourseDAO;
    private final MockTestGenerationOutputBoundary mockTestGenerationPresenter;
    private final MockTestGenerationTestDataAccessInterface mockTestGenerationDAO;

    public MockTestGenerationInteractor(MockTestGenerationCourseDataAccessInterface mockTestGenerationCourseDAO,
                                        MockTestGenerationTestDataAccessInterface  mockTestGenerationTestDAO,
                                        MockTestGenerationOutputBoundary mockTestGenerationPresenter) {
        this.mockTestGenerationPresenter = mockTestGenerationPresenter;
        this.mockTestGenerationCourseDAO = mockTestGenerationCourseDAO;
        this.mockTestGenerationDAO = mockTestGenerationTestDAO;
    }

    public void execute(MockTestGenerationInputData mockTestGenerationInputData) {
        try {
            mockTestGenerationPresenter.presentLoading();

            String courseId = mockTestGenerationInputData.getCourseID();

            List<PDFFile> courseMaterials = mockTestGenerationCourseDAO.getCourseMaterials(courseId);

            TestData testData = mockTestGenerationDAO.getTestData(courseMaterials);

            List<List<String>> choices = buildChoiceMatrix(testData.getQuestions(), testData.getQuestionTypes());
            testData.setChoices(choices);

            MockTestGenerationOutputData mockTestGenerationOutputData =
                    new MockTestGenerationOutputData(testData, courseId, choices);

            mockTestGenerationPresenter.presentTest(mockTestGenerationOutputData);
        } catch (Exception e) {
            mockTestGenerationPresenter.presentError(e.getMessage());
        }
    }

    private List<List<String>> buildChoiceMatrix(List<String> questions, List<String> questionTypes) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<String>> choices = new ArrayList<>(questions.size());
        for (int i = 0; i < questions.size(); i++) {
            String questionType = "";
            if (questionTypes != null && questionTypes.size() > i && questionTypes.get(i) != null) {
                questionType = questionTypes.get(i);
            }

            if ("Multiple Choice".equalsIgnoreCase(questionType.trim())) {
                choices.add(extractMultipleChoiceOptions(questions.get(i)));
            } else {
                choices.add(Collections.emptyList());
            }
        }

        return choices;
    }

    private List<String> extractMultipleChoiceOptions(String question) {
        if (question == null || question.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> options = new ArrayList<>();
        Matcher matcher = MULTIPLE_CHOICE_OPTION_PATTERN.matcher(question);
        while (matcher.find()) {
            options.add(matcher.group(1).trim());
        }

        if (!options.isEmpty()) {
            return options;
        }

        // Fallback: split lines and collect those starting with A-D.
        String[] lines = question.split("\\R");
        for (String line : lines) {
            Matcher lineMatcher = MULTIPLE_CHOICE_OPTION_PATTERN.matcher(line);
            if (lineMatcher.find()) {
                options.add(lineMatcher.group(1).trim());
            }
        }

        return options.isEmpty() ? Collections.emptyList() : options;
    }
}
