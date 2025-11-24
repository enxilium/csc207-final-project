package usecases;

public class GenerateFlashcardsRequestModel {
    private final String courseName;
    private final String content;

    public GenerateFlashcardsRequestModel(String courseName, String content) {
        this.courseName = courseName;
        this.content = content;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getContent() {
        return content;
    }
}