package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * A domain entity representing a mock test.
 * Compatible with Gson for JSON deserialization.
 */
public class TestData {

    private List<String> questions;
    private List<String> answers;
    private List<String> questionTypes;
    private List<List<String>> choices = new ArrayList<>();

    /**
     * Empty, default constructor for GSON.
     */
    public TestData() {}

    public TestData(List<String> questions, List<String> answers, List<String> questionTypes) {
        this.questions = questions;
        this.answers = answers;
        this.questionTypes = questionTypes;
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

    public List<List<String>> getChoices() {
        return choices;
    }

    public void setChoices(List<List<String>> choices) {
        this.choices = choices;
    }
}