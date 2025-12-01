package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecases.Timeline.ViewTimelineResponse;
import interface_adapters.timeline.ViewTimelineSwingPresenter;
import interface_adapters.timeline.ViewTimelineViewModel;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ViewTimelineSwingPresenterTest {
    private ViewTimelineViewModel viewModel;
    private ViewTimelineSwingPresenter presenter;

    @BeforeEach
    void setUp() {
        viewModel = new ViewTimelineViewModel();
        presenter = new ViewTimelineSwingPresenter(viewModel);
    }

    @Test
    void testPresent() {
        ViewTimelineResponse response = new ViewTimelineResponse();
        UUID courseId = UUID.randomUUID();
        response.setCourseId(courseId);
        response.setEmpty(false);

        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        card.setTitle("Test");
        response.getItems().add(card);

        presenter.present(response);

        assertEquals(courseId, viewModel.getCourseId());
        assertFalse(viewModel.isEmpty());
        assertEquals(1, viewModel.getItems().size());
    }

    @Test
    void testPresentNotFound() {
        presenter.presentNotFound("No items found");

        assertTrue(viewModel.isEmpty());
        assertEquals(1, viewModel.getItems().size());
        ViewTimelineResponse.TimelineCardVM card = viewModel.getItems().get(0);
        assertEquals("Info", card.getTitle());
        assertEquals("No items found", card.getSubtitle());
    }
}


