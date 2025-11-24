package entities;

import java.util.List;

public class EvaluationData {

    private List<String> questions;
    private List<String> answers;
    private List<String> userAnswers;
    private List<String> correctness;
    private List<String> feedback;
    private int score;

    /**
     * Empty, default constructor for GSON.
     */
    public EvaluationData() {}

    public EvaluationData(List<String> questions, List<String> answers, List<String> userAnswers,
                          List<String> correctness, List<String> feedback, int score) {
        this.questions = questions;
        this.answers = answers;
        this.userAnswers = userAnswers;
        this.correctness = correctness;
        this.feedback = feedback;
        this.score = score;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public List<String> getCorrectness() {
        return correctness;
    }

    public int getScore() {
        return score;
    }
}