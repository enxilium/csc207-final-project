package Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;

/**
 * Simple test implementation of ITimelineRepository for testing purposes.
 */
public class TestTimelineRepository implements ITimelineRepository {
    private final Map<UUID, List<TimelineEvent>> eventsByCourse = new HashMap<>();

    @Override
    public void save(TimelineEvent event) {
        eventsByCourse.computeIfAbsent(event.getCourseId(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<TimelineEvent> findByCourseNewestFirst(UUID courseId) {
        List<TimelineEvent> events = eventsByCourse.getOrDefault(courseId, new ArrayList<>());
        List<TimelineEvent> reversed = new ArrayList<>(events);
        Collections.reverse(reversed);
        return reversed;
    }
}


