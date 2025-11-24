package interface_adapters.evaluate_test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvaluateTestState {
    // The full test data
    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>(); // The correct answers
    private int currentQuestionIndex = 0; // -1 means evaluation is not active
    private List<String> userAnswers; // user provided answers
    private List<String> correctness; // integer indicating whether the user answer was correct or not.
    private List<String> feedback; // String feedback if necessary
    private int score;


    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public List<String> getCorrectness() {
        return correctness;
    }

    public void setCorrectness(List<String> correctness) {
        this.correctness = correctness;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }
}

