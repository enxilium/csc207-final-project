package Timeline;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ViewTimelineResponseTest {

    @Test
    void testGettersAndSetters() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        UUID courseId = UUID.randomUUID();

        response.setCourseId(courseId);
        response.setEmpty(false);

        assertEquals(courseId, response.getCourseId());
        assertFalse(response.isEmpty());
        
        // Test setEmpty(true) to cover both branches
        response.setEmpty(true);
        assertTrue(response.isEmpty());
        
        // Test setCourseId with null
        response.setCourseId(null);
        assertNull(response.getCourseId());
    }

    @Test
    void testItemsList() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        card.setTitle("Test");

        List<ViewTimelineResponse.TimelineCardVM> items = new ArrayList<>();
        items.add(card);
        response.setItems(items);

        assertEquals(1, response.getItems().size());
        assertEquals(card, response.getItems().get(0));
        
        // Test setItems with null to ensure it's handled
        response.setItems(null);
        assertNull(response.getItems());
        
        // Test setItems with empty list
        response.setItems(new ArrayList<>());
        assertNotNull(response.getItems());
        assertEquals(0, response.getItems().size());
    }

    @Test
    void testTimelineCardVM() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        UUID contentId = UUID.randomUUID();

        card.setTime("Jan 1, 12:00");
        card.setIcon("notes");
        card.setType("NOTES");
        card.setTitle("Test Title");
        card.setSubtitle("Test Subtitle");
        card.setSnippet("Test snippet");
        card.setContentId(contentId);
        card.setEventId("event-id");

        assertEquals("Jan 1, 12:00", card.getTime());
        assertEquals("notes", card.getIcon());
        assertEquals("NOTES", card.getType());
        assertEquals("Test Title", card.getTitle());
        assertEquals("Test Subtitle", card.getSubtitle());
        assertEquals("Test snippet", card.getSnippet());
        assertEquals(contentId, card.getContentId());
        assertEquals("event-id", card.getEventId());
    }

    @Test
    void testConstructorsAndInitialization() {
        // Test outer class constructor and initialization
        ViewTimelineResponse response = new ViewTimelineResponse();
        assertNotNull(response);
        assertNull(response.getCourseId());
        assertFalse(response.isEmpty());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        
        // Test inner class constructor and default values
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        assertNotNull(card);
        assertNull(card.getTime());
        assertNull(card.getIcon());
        assertNull(card.getType());
        assertNull(card.getTitle());
        assertNull(card.getSubtitle());
        assertNull(card.getSnippet());
        assertNull(card.getContentId());
        assertNull(card.getEventId());
        
        // Test adding items
        response.getItems().add(card);
        assertEquals(1, response.getItems().size());
        
        // Test class metadata for coverage
        Class<?> cardClass = ViewTimelineResponse.TimelineCardVM.class;
        assertTrue(cardClass.isMemberClass());
        assertFalse(java.lang.reflect.Modifier.isStatic(cardClass.getModifiers()));
    }

    @Test
    void testTimelineCardVMGettersSetters() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        UUID contentId = UUID.randomUUID();

        // Test all setters and getters
        card.setTime("Jan 1, 12:00");
        card.setIcon("notes");
        card.setType("NOTES");
        card.setTitle("Test Title");
        card.setSubtitle("Test Subtitle");
        card.setSnippet("Test snippet");
        card.setContentId(contentId);
        card.setEventId("event-id");

        assertEquals("Jan 1, 12:00", card.getTime());
        assertEquals("notes", card.getIcon());
        assertEquals("NOTES", card.getType());
        assertEquals("Test Title", card.getTitle());
        assertEquals("Test Subtitle", card.getSubtitle());
        assertEquals("Test snippet", card.getSnippet());
        assertEquals(contentId, card.getContentId());
        assertEquals("event-id", card.getEventId());
        
        // Test null values
        card.setTime(null);
        card.setIcon(null);
        card.setType(null);
        card.setTitle(null);
        card.setSubtitle(null);
        card.setSnippet(null);
        card.setContentId(null);
        card.setEventId(null);
        
        assertNull(card.getTime());
        assertNull(card.getIcon());
        assertNull(card.getType());
        assertNull(card.getTitle());
        assertNull(card.getSubtitle());
        assertNull(card.getSnippet());
        assertNull(card.getContentId());
        assertNull(card.getEventId());
    }
}

