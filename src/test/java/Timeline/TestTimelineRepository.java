package Timeline;

import data_access.ITimelineRepository;
import entities.TimelineEvent;

import java.util.*;

public class TestTimelineRepository implements ITimelineRepository {
    private final Map<UUID, List<TimelineEvent>> byCourse = new HashMap<>();

    @Override
    public void save(TimelineEvent event) {
        byCourse.computeIfAbsent(event.getCourseId(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<TimelineEvent> findByCourseNewestFirst(UUID courseId) {
        List<TimelineEvent> list = byCourse.getOrDefault(courseId, new ArrayList<>());
        List<TimelineEvent> out = new ArrayList<>(list);
        Collections.reverse(out);
        return out;
    }
}

