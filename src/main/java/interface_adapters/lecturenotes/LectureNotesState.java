package interface_adapters.lecturenotes;

/**
 * State for the Lecture Notes view.
 * Stores current inputs, generated notes, and any error/loading info.
 */
public class LectureNotesState {

    private String courseId;
    private String topic;
    private String notesText;
    private String error;
    private boolean loading;

    public LectureNotesState() {
        this.courseId = "";
        this.topic = "";
        this.notesText = "";
        this.error = "";
        this.loading = false;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNotesText() {
        return notesText;
    }

    public void setNotesText(String notesText) {
        this.notesText = notesText;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}