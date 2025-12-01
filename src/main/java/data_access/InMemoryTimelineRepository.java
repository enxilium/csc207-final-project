package data_access;

import entities.TimelineEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory implementation of ITimelineRepository.
 * Hashmap is created to separate history by course.
 * This keeps things sorted and lets the user navigate accordingly.
 */
public class InMemoryTimelineRepository implements ITimelineRepository {
  private final Map<UUID, List<TimelineEvent>> byCourse = new HashMap<>();

  @Override
  public synchronized void save(TimelineEvent event) {
    byCourse.computeIfAbsent(event.getCourseId(), k -> new ArrayList<>())
        .add(event);
  }

  @Override
  public synchronized List<TimelineEvent> findByCourseNewestFirst(
      UUID courseId) {
    List<TimelineEvent> list = byCourse.getOrDefault(courseId,
        new ArrayList<>());
    List<TimelineEvent> out = new ArrayList<>(list);
    Collections.reverse(out);
    return out;
  }
}
