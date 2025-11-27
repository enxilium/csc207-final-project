package usecases.evaluate_test;

import entities.EvaluationData;

import java.util.List;

public class EvaluateTestOutputData {
    private final List<String> questions;
    private final List<String> answers;
    private final List<String> userAnswers;
    private final List<String> correctness;
    private final List<String> feedback;
    private final int score;


    public EvaluateTestOutputData(EvaluationData evalData) {
        this.questions = evalData.getQuestions();
        this.answers = evalData.getAnswers();
        this.userAnswers = evalData.getUserAnswers();
        this.correctness = evalData.getCorrectness();
        this.feedback = evalData.getFeedback();
        this.score = evalData.getScore();
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public List<String> getCorrectness() {
        return correctness;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public int getScore() {
        return score;
    }
}
