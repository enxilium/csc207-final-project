package Timeline;

import java.util.UUID;

public class TimelineController {
    private final ViewTimelineInputBoundary interactor;

    public TimelineController(ViewTimelineInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void open(UUID courseId) {
        interactor.execute(courseId);
    }
}
