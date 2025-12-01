package entities;

import java.time.LocalDateTime;

/**
 * Entity representing a set of generated lecture notes
 * for a particular course and topic.
 */
public class LectureNotes {

  private final String courseId;
  private final String topic;
  private final String content;      // the actual notes text (plain text / markdown)
  private final LocalDateTime generatedAt;

  /**
   * Constructs a LectureNotes with the given course ID, topic, content,
   * and generation timestamp.
   *
   * @param courseId the course ID
   * @param topic the topic
   * @param content the actual notes text (plain text / markdown)
   * @param generatedAt the generation timestamp
   */
  public LectureNotes(String courseId,
      String topic,
      String content,
      LocalDateTime generatedAt) {
    this.courseId = courseId;
    this.topic = topic;
    this.content = content;
    this.generatedAt = generatedAt;
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
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Gets the generation timestamp.
   *
   * @return the generation timestamp
   */
  public LocalDateTime getGeneratedAt() {
    return generatedAt;
  }
}
