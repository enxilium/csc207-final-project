package Timeline;

import usecases.Timeline.ViewTimelineInputBoundary;

import java.util.UUID;

/**
 * Simple test implementation of ViewTimelineInputBoundary for testing purposes.
 */
public class TestViewTimelineInputBoundary implements ViewTimelineInputBoundary {
    private UUID lastCourseId;
    private int executeCount = 0;

    @Override
    public void execute(UUID courseId) {
        this.lastCourseId = courseId;
        executeCount++;
    }

    public UUID getLastCourseId() {
        return lastCourseId;
    }

    public int getExecuteCount() {
        return executeCount;
    }
}


