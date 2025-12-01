package data_access;

import entities.TimelineEvent;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for timeline event data access operations.
 */
public interface ITimelineRepository {
  /**
   * Saves a timeline event.
   *
   * @param event the timeline event to save
   */
  void save(TimelineEvent event);

  /**
   * Finds timeline events for a course, ordered by newest first.
   *
   * @param courseId the course UUID
   * @return a list of timeline events, newest first
   */
  List<TimelineEvent> findByCourseNewestFirst(UUID courseId);
}
