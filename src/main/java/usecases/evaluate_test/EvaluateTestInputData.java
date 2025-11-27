package usecases.evaluate_test;

import java.util.List;

public class EvaluateTestInputData {
    private final String courseID;
    private final List<String> userAnswers;
    private final List<String> questions;
    private final List<String> answers;

    public EvaluateTestInputData(String courseID, List<String> userAnswers, List<String> questions,
                                 List<String> answers) {
        this.courseID = courseID;
        this.userAnswers = userAnswers;
        this.questions = questions;
        this.answers = answers;
    }

    public String getCourseID() {
        return courseID;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
