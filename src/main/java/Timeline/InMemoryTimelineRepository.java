package Timeline;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;

// Hashmap is created to seperate history by course.
// This keeps things sorted and lets the user navigate accordingly.
public class InMemoryTimelineRepository implements ITimelineRepository {
    private final Map<UUID, List<TimelineEvent>> byCourse = new HashMap<>();

    @Override
    public synchronized void save(TimelineEvent event) {
        byCourse.computeIfAbsent(event.getCourseId(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public synchronized List<TimelineEvent> findByCourseNewestFirst(UUID courseId) {
        List<TimelineEvent> list = byCourse.getOrDefault(courseId, new ArrayList<>());
        List<TimelineEvent> out = new ArrayList<>(list);
        Collections.reverse(out);
        return out;
    }
}
