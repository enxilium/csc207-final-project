package usecases.mock_test_generation;

import entities.TestData;

import java.util.List;

public class MockTestGenerationOutputData {
    private final List<String> questions;
    private final List<String> answers;
    private final List<String> questionTypes;
    private final String courseId;
    private final List<List<String>> choices;

    public MockTestGenerationOutputData(TestData testData, String courseId, List<List<String>> choices) {
        this.questions = testData.getQuestions();
        this.answers = testData.getAnswers();
        this.questionTypes = testData.getQuestionTypes();
        this.courseId = courseId;
        this.choices = choices;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getQuestionTypes() {
        return questionTypes;
    }

    public String getCourseId() {
        return courseId;
    }

    public List<List<String>> getChoices() {
        return choices;
    }
}
