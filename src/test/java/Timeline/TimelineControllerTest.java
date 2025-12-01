package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interface_adapters.timeline.TimelineController;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimelineControllerTest {
    private TestViewTimelineInputBoundary interactor;
    private TimelineController controller;

    @BeforeEach
    void setUp() {
        interactor = new TestViewTimelineInputBoundary();
        controller = new TimelineController(interactor);
    }

    @Test
    void testOpen() {
        UUID courseId = UUID.randomUUID();
        controller.open(courseId);

        assertEquals(courseId, interactor.getLastCourseId());
        assertEquals(1, interactor.getExecuteCount());
    }

    @Test
    void testOpenMultipleTimes() {
        UUID courseId1 = UUID.randomUUID();
        UUID courseId2 = UUID.randomUUID();

        controller.open(courseId1);
        controller.open(courseId2);

        assertEquals(courseId2, interactor.getLastCourseId());
        assertEquals(2, interactor.getExecuteCount());
    }
}


