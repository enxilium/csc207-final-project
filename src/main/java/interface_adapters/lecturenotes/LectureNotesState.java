package interface_adapters.lecturenotes;

public class LectureNotesState {
    private String courseId = "";
    private String topic = "";
    private String notesText = "";
    private String error = "";
    private boolean loading = false;

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getNotesText() { return notesText; }
    public void setNotesText(String notesText) { this.notesText = notesText; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public boolean isLoading() { return loading; }
    public void setLoading(boolean loading) { this.loading = loading; }
}