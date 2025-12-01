package interface_adapters.lecturenotes;

/**
 * State class for the lecture notes view model.
 */
public class LectureNotesState {
  private String courseId = "";
  private String topic = "";
  private String notesText = "";
  private String error = "";
  private boolean loading = false;

  /**
   * Gets the course ID.
   *
   * @return the course ID
   */
  public String getCourseId() {
    return courseId;
  }

  /**
   * Sets the course ID.
   *
   * @param courseId the course ID to set
   */
  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  /**
   * Gets the topic.
   *
   * @return the topic
   */
  public String getTopic() {
    return topic;
  }

  /**
   * Sets the topic.
   *
   * @param topic the topic to set
   */
  public void setTopic(String topic) {
    this.topic = topic;
  }

  /**
   * Gets the notes text.
   *
   * @return the notes text
   */
  public String getNotesText() {
    return notesText;
  }

  /**
   * Sets the notes text.
   *
   * @param notesText the notes text to set
   */
  public void setNotesText(String notesText) {
    this.notesText = notesText;
  }

  /**
   * Gets the error message.
   *
   * @return the error message
   */
  public String getError() {
    return error;
  }

  /**
   * Sets the error message.
   *
   * @param error the error message to set
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * Checks if notes are currently loading.
   *
   * @return true if loading
   */
  public boolean isLoading() {
    return loading;
  }

  /**
   * Sets the loading state.
   *
   * @param loading whether notes are loading
   */
  public void setLoading(boolean loading) {
    this.loading = loading;
  }
}
