package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecases.Timeline.ViewTimelineResponse;
import interface_adapters.timeline.ViewTimelineViewModel;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ViewTimelineViewModelTest {
    private ViewTimelineViewModel viewModel;
    private boolean propertyChangeFired;
    private PropertyChangeEvent lastEvent;

    @BeforeEach
    void setUp() {
        viewModel = new ViewTimelineViewModel();
        propertyChangeFired = false;
        lastEvent = null;
    }

    @Test
    void testInitialState() {
        assertTrue(viewModel.isEmpty());
        assertTrue(viewModel.getItems().isEmpty());
        assertEquals("timeline", viewModel.getViewName());
    }

    @Test
    void testSetFromResponse() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        UUID courseId = UUID.randomUUID();
        response.setCourseId(courseId);
        response.setEmpty(false);

        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        card.setTitle("Test");
        response.getItems().add(card);

        viewModel.addPropertyChangeListener(evt -> {
            propertyChangeFired = true;
            lastEvent = evt;
        });

        viewModel.setFromResponse(response);

        assertEquals(courseId, viewModel.getCourseId());
        assertFalse(viewModel.isEmpty());
        assertEquals(1, viewModel.getItems().size());
        assertTrue(propertyChangeFired);
        assertEquals("timeline", lastEvent.getPropertyName());
    }

    @Test
    void testGetItemsReturnsNewList() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        response.getItems().add(card);
        viewModel.setFromResponse(response);

        List<ViewTimelineResponse.TimelineCardVM> items1 = viewModel.getItems();
        List<ViewTimelineResponse.TimelineCardVM> items2 = viewModel.getItems();

        assertNotSame(items1, items2);
        assertEquals(items1, items2);
    }

    @Test
    void testEmptyResponse() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        response.setEmpty(true);
        
        viewModel.setFromResponse(response);
        
        assertTrue(viewModel.isEmpty());
        assertTrue(viewModel.getItems().isEmpty());
    }

    @Test
    void testSetCourseId() {
        UUID courseId1 = UUID.randomUUID();
        UUID courseId2 = UUID.randomUUID();
        
        viewModel.setCourseId(courseId1);
        assertEquals(courseId1, viewModel.getCourseId());
        
        viewModel.setCourseId(courseId2);
        assertEquals(courseId2, viewModel.getCourseId());
    }

    @Test
    void testSetCourseIdNull() {
        UUID courseId = UUID.randomUUID();
        viewModel.setCourseId(courseId);
        assertEquals(courseId, viewModel.getCourseId());
        
        viewModel.setCourseId(null);
        assertNull(viewModel.getCourseId());
    }

    @Test
    void testGetCourseIdInitiallyNull() {
        assertNull(viewModel.getCourseId());
    }
}


