package usecases.lecturenotes;

public class GenerateLectureNotesInputData {
    private final String courseId;
    private final String topic;

    public GenerateLectureNotesInputData(String courseId, String topic) {
        this.courseId = courseId;
        this.topic = topic;
    }

    public String getCourseId() { return courseId; }
    public String getTopic() { return topic; }
}