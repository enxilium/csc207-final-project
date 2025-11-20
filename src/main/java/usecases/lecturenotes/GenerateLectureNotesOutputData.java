package usecases.lecturenotes;

public class GenerateLectureNotesOutputData {
    private final String courseId;
    private final String topic;
    private final String notesText;

    public GenerateLectureNotesOutputData(String courseId, String topic, String notesText) {
        this.courseId = courseId;
        this.topic = topic;
        this.notesText = notesText;
    }

    public String getCourseId() { return courseId; }
    public String getTopic() { return topic; }
    public String getNotesText() { return notesText; }
}