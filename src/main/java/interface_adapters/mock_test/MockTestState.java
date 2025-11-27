package interface_adapters.mock_test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockTestState {
    // The full test data
    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>(); // The correct answers
    private List<String> questionTypes = new ArrayList<>();
    private List<List<String>> choices = new ArrayList<>();
    private String courseId = "";

    // The current state of the user's session
    private int currentQuestionIndex = -1; // -1 means test is not active
    private List<String> userAnswers; // To store what the user types

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setQuestionTypes(List<String> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public void setChoices(List<List<String>> choices) {
        this.choices = choices;
    }

    public void setCurrentQuestionIndex(int index) {
        this.currentQuestionIndex = index;
    }

    // This is important for a new test
    public void initializeUserAnswers(int numQuestions) {
        this.userAnswers = new ArrayList<>(Collections.nCopies(numQuestions, ""));
    }

    public List<String> getQuestions() {
        return questions;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
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

    // The view will call this to update the user's answer for the current question
    public void setUserAnswerForCurrentQuestion(String answer) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < userAnswers.size()) {
            this.userAnswers.set(currentQuestionIndex, answer);
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
