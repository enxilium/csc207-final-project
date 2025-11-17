package Timeline;
import java.util.List;
import java.util.UUID;

public interface ITimelineRepository {
    void save(TimelineEvent event);
    List<TimelineEvent> findByCourseNewestFirst(UUID courseId);
}
