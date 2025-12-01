package usecases.lecturenotes;

/**
 * Output data model for lecture notes generation.
 */
public class GenerateLectureNotesOutputData {
  private final String courseId;
  private final String topic;
  private final String notesText;

  /**
   * Constructs a GenerateLectureNotesOutputData with the given course ID,
   * topic, and notes text.
   *
   * @param courseId the course ID
   * @param topic the topic
   * @param notesText the notes text content
   */
  public GenerateLectureNotesOutputData(String courseId, String topic,
      String notesText) {
    this.courseId = courseId;
    this.topic = topic;
    this.notesText = notesText;
  }

  /**
   * Gets the course ID.
   *
   * @return the course ID
   */
  public String getCourseId() {
    return courseId;
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
   * Gets the notes text.
   *
   * @return the notes text
   */
  public String getNotesText() {
    return notesText;
  }
}
