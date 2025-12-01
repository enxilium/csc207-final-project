package usecases.lecturenotes;

/**
 * Input data model for lecture notes generation.
 */
public class GenerateLectureNotesInputData {
  private final String courseId;
  private final String topic;

  /**
   * Constructs a GenerateLectureNotesInputData with the given course ID
   * and topic.
   *
   * @param courseId the course ID
   * @param topic the topic for the lecture notes
   */
  public GenerateLectureNotesInputData(String courseId, String topic) {
    this.courseId = courseId;
    this.topic = topic;
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
}
