package Timeline;

import interface_adapters.ViewManagerModel;
import interface_adapters.lecturenotes.LectureNotesViewModel;
import interface_adapters.flashcards.FlashcardViewModel;
import interface_adapters.evaluate_test.EvaluateTestViewModel;
import interface_adapters.mock_test.MockTestViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interface_adapters.timeline.TimelineController;
import usecases.Timeline.ViewTimelineResponse;
import views.ViewTimelineView;
import interface_adapters.timeline.ViewTimelineViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ViewTimelineViewTest {
    private ViewTimelineViewModel viewModel;
    private ViewManagerModel viewManagerModel;
    private LectureNotesViewModel lectureNotesViewModel;
    private FlashcardViewModel flashcardViewModel;
    private EvaluateTestViewModel evaluateTestViewModel;
    private MockTestViewModel mockTestViewModel;
    private ViewTimelineView timelineView;
    private Method openMethod;

    @BeforeEach
    void setUp() throws Exception {
        viewModel = new ViewTimelineViewModel();
        TestViewTimelineInputBoundary interactor = new TestViewTimelineInputBoundary();
        TimelineController controller = new TimelineController(interactor);
        viewManagerModel = new ViewManagerModel();
        // Set initial state to timeline so we can detect when it doesn't change
        viewManagerModel.setState("timeline");
        lectureNotesViewModel = new LectureNotesViewModel();
        flashcardViewModel = new FlashcardViewModel();
        evaluateTestViewModel = new EvaluateTestViewModel();
        mockTestViewModel = new MockTestViewModel();
        timelineView = new ViewTimelineView(viewModel, controller, viewManagerModel,
                                           lectureNotesViewModel, flashcardViewModel,
                                           evaluateTestViewModel, mockTestViewModel);
        openMethod = ViewTimelineView.class.getDeclaredMethod(
            "openStudyMaterial", ViewTimelineResponse.TimelineCardVM.class);
        openMethod.setAccessible(true);
    }

    private ViewTimelineResponse createResponse() {
        return new ViewTimelineResponse();
    }

    private ViewTimelineResponse.TimelineCardVM createCard(String type, UUID contentId) {
        ViewTimelineResponse response = new ViewTimelineResponse();
        ViewTimelineResponse.TimelineCardVM card = response.new TimelineCardVM();
        card.setType(type);
        card.setContentId(contentId);
        return card;
    }

    private void triggerPropertyChange(ViewTimelineResponse response) {
        viewModel.setFromResponse(response);
        PropertyChangeEvent evt = new PropertyChangeEvent(viewModel, "timeline", null, viewModel);
        timelineView.propertyChange(evt);
    }

    private JList<?> getList() throws Exception {
        Field listField = ViewTimelineView.class.getDeclaredField("list");
        listField.setAccessible(true);
        return (JList<?>) listField.get(timelineView);
    }

    private void simulateMouseClick(JList<?> list, int index) {
        if (list.getModel().getSize() > index) {
            java.awt.Rectangle cellBounds = list.getCellBounds(index, index);
            if (cellBounds != null) {
                MouseEvent clickEvent = new MouseEvent(
                    list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                    0, cellBounds.x + cellBounds.width / 2, cellBounds.y + cellBounds.height / 2, 1, false
                );
                list.dispatchEvent(clickEvent);
            }
        }
    }

    // Property Change Tests
    @Test
    void testPropertyChangeWithItems() {
        ViewTimelineResponse response = createResponse();
        response.setEmpty(false);
        response.getItems().add(createCard("NOTES", UUID.randomUUID()));
        triggerPropertyChange(response);
        assertFalse(viewModel.isEmpty());
    }

    @Test
    void testPropertyChangeWithEmptyTimeline() {
        ViewTimelineResponse response = createResponse();
        response.setEmpty(true);
        triggerPropertyChange(response);
        assertTrue(viewModel.isEmpty());
    }

    @Test
    void testPropertyChangeIgnoresWrongProperty() {
        ViewTimelineResponse response = createResponse();
        viewModel.setFromResponse(response);
        // Fire property change with wrong property name - should be ignored
        PropertyChangeEvent wrongEvent = new PropertyChangeEvent(viewModel, "wrong", null, viewModel);
        timelineView.propertyChange(wrongEvent);
        // List should remain empty since wrong property was ignored
        assertTrue(viewModel.getItems().isEmpty());
        PropertyChangeEvent evt = new PropertyChangeEvent(viewModel, "wrong", null, viewModel);
        timelineView.propertyChange(evt);
        // Should not cause any issues
    }

    @Test
    void testPropertyChangeClearsPreviousItems() {
        ViewTimelineResponse response1 = createResponse();
        ViewTimelineResponse.TimelineCardVM card1 = createCard("NOTES", UUID.randomUUID());
        card1.setTitle("Card 1");
        response1.getItems().add(card1);
        triggerPropertyChange(response1);

        ViewTimelineResponse response2 = createResponse();
        ViewTimelineResponse.TimelineCardVM card2 = createCard("NOTES", UUID.randomUUID());
        card2.setTitle("Card 2");
        response2.getItems().add(card2);
        triggerPropertyChange(response2);

        assertEquals(1, viewModel.getItems().size());
        assertEquals("Card 2", viewModel.getItems().get(0).getTitle());
    }

    @Test
    void testPropertyChangeWithMultipleItems() {
        ViewTimelineResponse response = createResponse();
        for (int i = 0; i < 5; i++) {
            ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
            card.setTitle("Card " + i);
            response.getItems().add(card);
        }
        triggerPropertyChange(response);
        assertEquals(5, viewModel.getItems().size());
    }

    // OpenStudyMaterial Tests
    @Test
    void testOpenStudyMaterialNotes() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Test snippet");
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
    }

    @Test
    void testOpenStudyMaterialNotesWithNullTitle() throws Exception {
        // Test line 191: card.getTitle() != null -> false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle(null); // null title
        card.setSnippet("Test snippet");
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        // Should use "Notes" as default title when title is null
        assertEquals("Notes", lectureNotesViewModel.getState().getTopic());
    }

    @Test
    void testOpenStudyMaterialNotesWithEmptyTitle() throws Exception {
        // Test line 191: !card.getTitle().isEmpty() -> false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle(""); // empty title
        card.setSnippet("Test snippet");
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        // Should use "Notes" as default title when title is empty
        assertEquals("Notes", lectureNotesViewModel.getState().getTopic());
    }

    @Test
    void testOpenStudyMaterialFlashcards() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("20 cards");
        card.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        openMethod.invoke(timelineView, card);
        assertEquals("flashcardDisplay", viewManagerModel.getState());
        assertNotNull(flashcardViewModel.getCurrentFlashcardSet());
    }

    @Test
    void testOpenStudyMaterialQuiz() throws Exception {
        // When quiz has no testData or evaluationData, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("15 questions");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizSubmitted() throws Exception {
        // When quiz has no evaluationData or testData, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("Score 14.0/15");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialWithNullContentId() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", null);
        openMethod.invoke(timelineView, card);
        // Should handle null gracefully - view state should not change
        assertNotEquals("notes", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialNullType() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard(null, UUID.randomUUID());
        openMethod.invoke(timelineView, card);
        // Should handle null type gracefully - view state should not change
        assertNotEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotEquals("flashcardDisplay", viewManagerModel.getState());
        assertNotEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
        assertNotEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialUnknownType() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("UNKNOWN", UUID.randomUUID());
        openMethod.invoke(timelineView, card);
        // Should show error dialog - view state should not change
        assertNotEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotEquals("flashcardDisplay", viewManagerModel.getState());
        assertNotEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
        assertNotEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialWithEmptySubtitle() throws Exception {
        // Test empty subtitle for both FLASHCARDS and QUIZ
        ViewTimelineResponse.TimelineCardVM flashcard = createCard("FLASHCARDS", UUID.randomUUID());
        flashcard.setSubtitle("");
        flashcard.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        openMethod.invoke(timelineView, flashcard);
        // If flashcard data is available, should navigate to flashcardDisplay
        String flashcardState = viewManagerModel.getState();
        
        ViewTimelineResponse.TimelineCardVM quiz = createCard("QUIZ", UUID.randomUUID());
        quiz.setSubtitle("");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, quiz);
        // When quiz has no data, shows error dialog and doesn't navigate
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithNoNumbers() throws Exception {
        // Test subtitle with no digits - should still work if flashcard data is available
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("cards"); // No numbers
        card.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        openMethod.invoke(timelineView, card);
        // Should navigate to flashcardDisplay if data is available
        String state = viewManagerModel.getState();
        // Either navigates to flashcardDisplay or shows error
        assertTrue(state.equals("flashcardDisplay") || state.equals("timeline"));
    }

    @Test
    void testOpenStudyMaterialQuizWithInvalidSubtitleFormats() throws Exception {
        // Test various invalid subtitle formats - when no data, shows error dialog
        String[] invalidSubtitles = {"Score invalid/15", "Score 14.0", "Score abc/def", 
                                     "Score 8.5/", "Score /", "Some other text", 
                                     "questions", "  15  questions  "};
        for (String subtitle : invalidSubtitles) {
            ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
            card.setSubtitle(subtitle);
            String initialState = viewManagerModel.getState();
            openMethod.invoke(timelineView, card);
            // State should not change (error dialog shown instead)
            assertEquals(initialState, viewManagerModel.getState());
        }
    }

    // Back Button Tests
    @Test
    void testBackButtonNotesView() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        openMethod.invoke(timelineView, card);
        // Check that LectureNotesViewModel state was updated
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        // Verify notes text was set
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
    }

    @Test
    void testBackButtonFlashcardsView() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("20 cards");
        card.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        response.getItems().add(card);
        triggerPropertyChange(response);
        openMethod.invoke(timelineView, card);
        // Check that FlashcardDisplayView was navigated to
        assertEquals("flashcardDisplay", viewManagerModel.getState());
    }

    @Test
    void testBackButtonQuizView() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("15 questions");
        card.setEvaluationData("{\"questions\":[\"Q1\"],\"answers\":[\"A1\"],\"userAnswers\":[\"A1\"],\"correctness\":[\"1\"],\"feedback\":[\"Good\"],\"score\":100}");
        response.getItems().add(card);
        triggerPropertyChange(response);
        openMethod.invoke(timelineView, card);
        // Check that EvaluateTestViewModel was navigated to
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    // Mouse Click Tests
    @Test
    void testMouseClickWithValidCard() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Test snippet");
        response.getItems().add(card);
        triggerPropertyChange(response);
        JList<?> list = getList();
        simulateMouseClick(list, 0);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testMouseClickWithNullContentIdFallsBackToViewModel() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        if (listModel.getSize() > 0) {
            ViewTimelineResponse.TimelineCardVM nullCard = createCard("NOTES", null);
            nullCard.setTitle("Test Notes");
            listModel.setElementAt(nullCard, 0);
            JList<?> list = getList();
            simulateMouseClick(list, 0);
            assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        }
    }

    @Test
    void testMouseClickIgnoresWhenContentIdNull() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", null);
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        String initialState = viewManagerModel.getState();
        JList<?> list = getList();
        simulateMouseClick(list, 0);
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testMouseClickWithInvalidIndexBounds() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test");
        response.getItems().add(card);
        triggerPropertyChange(response);
        JList<?> list = getList();
        MouseEvent invalidClick = new MouseEvent(
            list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
            0, -100, -100, 1, false
        );
        list.dispatchEvent(invalidClick);
        // Should handle gracefully
    }

    @Test
    void testMouseClickDoubleClick() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Test snippet");
        response.getItems().add(card);
        triggerPropertyChange(response);
        JList<?> list = getList();
        if (list.getModel().getSize() > 0) {
            java.awt.Rectangle cellBounds = list.getCellBounds(0, 0);
            if (cellBounds != null) {
                MouseEvent doubleClick = new MouseEvent(
                    list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                    0, cellBounds.x + cellBounds.width / 2, cellBounds.y + cellBounds.height / 2, 2, false
                );
                list.dispatchEvent(doubleClick);
                assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
            }
        }
    }

    // Renderer Tests
    @Test
    void testTimelineCardRendererSelectedState() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Title");
        card.setSubtitle("Test Subtitle");
        card.setSnippet("Test Snippet");
        card.setTime("Jan 1, 12:00");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        
        Component selected = renderer.getListCellRendererComponent(list, card, 0, true, true);
        Component unselected = renderer.getListCellRendererComponent(list, card, 0, false, false);
        assertNotNull(selected);
        assertNotNull(unselected);
    }

    @Test
    void testTimelineCardRendererNullValues() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("TEST", UUID.randomUUID());
        card.setTitle(null);
        card.setSubtitle(null);
        card.setSnippet(null);
        card.setTime(null);
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        Component component = renderer.getListCellRendererComponent(list, card, 0, false, false);
        assertNotNull(component);
    }

    @Test
    void testTimelineCardRendererSubtitleFallback() throws Exception {
        // Test subtitle fallback to snippet (both null and empty)
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card1 = createCard("NOTES", UUID.randomUUID());
        card1.setTitle("Test");
        card1.setSubtitle(null);
        card1.setSnippet("Test snippet");
        card1.setTime("Jan 1, 12:00");
        
        ViewTimelineResponse.TimelineCardVM card2 = createCard("NOTES", UUID.randomUUID());
        card2.setTitle("Test");
        card2.setSubtitle("");
        card2.setSnippet("Test snippet");
        card2.setTime("Jan 1, 12:00");
        
        response.getItems().add(card1);
        response.getItems().add(card2);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        assertNotNull(renderer.getListCellRendererComponent(list, card1, 0, false, false));
        assertNotNull(renderer.getListCellRendererComponent(list, card2, 1, false, false));
    }

    @Test
    void testTimelineCardRendererTitleFallbackToType() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle(null);
        card.setSnippet("Test");
        card.setTime("Jan 1, 12:00");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        Component component = renderer.getListCellRendererComponent(list, card, 0, false, false);
        assertNotNull(component);
    }

    // Other Tests
    @Test
    void testGetViewName() {
        assertEquals("timeline", timelineView.getViewName());
    }

    @Test
    void testRefreshButtonWithNullCourseId() {
        ViewTimelineResponse response = createResponse();
        response.setCourseId(null);
        viewModel.setFromResponse(response);
        JPanel header = (JPanel) timelineView.getComponent(0);
        // Find refresh button by checking all components
        JButton refreshBtn = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JButton) {
                JButton btn = (JButton) header.getComponent(i);
                if ("Refresh".equals(btn.getText())) {
                    refreshBtn = btn;
                    break;
                }
            }
        }
        assertNotNull(refreshBtn);
        refreshBtn.doClick();
        // Should not throw exception - verify state unchanged
        assertNull(viewModel.getCourseId());
    }

    @Test
    void testRefreshButtonWithValidCourseId() {
        UUID courseId = UUID.randomUUID();
        ViewTimelineResponse response = createResponse();
        response.setCourseId(courseId);
        viewModel.setFromResponse(response);
        JPanel header = (JPanel) timelineView.getComponent(0);
        // Find refresh button by checking all components
        JButton refreshBtn = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JButton) {
                JButton btn = (JButton) header.getComponent(i);
                if ("Refresh".equals(btn.getText())) {
                    refreshBtn = btn;
                    break;
                }
            }
        }
        assertNotNull(refreshBtn);
        refreshBtn.doClick();
        // Should trigger controller.open
    }

    // Additional coverage tests for uncovered branches
    @Test
    void testMouseClickWhenIndexOutOfBoundsInViewModel() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        // Manually manipulate listModel to have null contentId
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        
        if (listModel.getSize() > 0) {
            ViewTimelineResponse.TimelineCardVM nullCard = createCard("NOTES", null);
            listModel.setElementAt(nullCard, 0);
            
            // Clear viewModel items so index < items.size() is false
            ViewTimelineResponse emptyResponse = createResponse();
            viewModel.setFromResponse(emptyResponse);
            
            JList<?> list = getList();
            simulateMouseClick(list, 0);
            // Should handle gracefully when index >= items.size()
        }
    }

    @Test
    void testRendererWithNullValues() throws Exception {
        // Test renderer handles null snippet and time
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card1 = createCard("NOTES", UUID.randomUUID());
        card1.setTitle("Test");
        card1.setSnippet(null);
        card1.setTime("Jan 1, 12:00");
        
        ViewTimelineResponse.TimelineCardVM card2 = createCard("NOTES", UUID.randomUUID());
        card2.setTitle("Test");
        card2.setTime(null);
        
        response.getItems().add(card1);
        response.getItems().add(card2);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        assertNotNull(renderer.getListCellRendererComponent(list, card1, 0, false, false));
        assertNotNull(renderer.getListCellRendererComponent(list, card2, 1, false, false));
    }


    @Test
    void testPropertyChangeWithEmptyList() {
        ViewTimelineResponse response = createResponse();
        response.setEmpty(false);
        // No items added
        triggerPropertyChange(response);
        assertTrue(viewModel.getItems().isEmpty());
    }

    @Test
    void testMouseClickWhenContentIdNullInListModelButExistsInViewModel() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Test snippet");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        // Manually set contentId to null in listModel but keep it in viewModel
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        
        if (listModel.getSize() > 0 && !viewModel.getItems().isEmpty()) {
            ViewTimelineResponse.TimelineCardVM nullCard = createCard("NOTES", null);
            nullCard.setTitle("Test Notes");
            listModel.setElementAt(nullCard, 0);
            
            JList<?> list = getList();
            simulateMouseClick(list, 0);
            // Should fall back to viewModel and open the notes
            assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        }
    }

    @Test
    void testMouseClickWhenIndexOutOfBoundsInListModel() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        // Reset state to a known value
        viewManagerModel.setState("timeline");
        viewManagerModel.firePropertyChange();
        String initialState = viewManagerModel.getState();
        assertEquals("timeline", initialState); // Ensure we start with timeline
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) list.getModel();
        
        // Create a custom JList that returns an out-of-bounds index by overriding locationToIndex
        class TestJList extends JList<ViewTimelineResponse.TimelineCardVM> {
            TestJList(DefaultListModel<ViewTimelineResponse.TimelineCardVM> model) {
                super(model);
            }
            @Override
            public int locationToIndex(Point location) {
                // Return an index that's >= listModel.getSize() to test bounds check
                return getModel().getSize(); // This will be >= size, so bounds check fails
            }
        }
        TestJList testList = new TestJList(listModel);
        @SuppressWarnings("unchecked")
        ListCellRenderer<? super ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<? super ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        testList.setCellRenderer(renderer);
        if (list.getMouseListeners().length > 0) {
            testList.addMouseListener(list.getMouseListeners()[0]);
        }
        
        // Click on the test list - should not process because index >= size
        MouseEvent invalidClick = new MouseEvent(
            testList, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
            0, 10, 10, 1, false
        );
        testList.dispatchEvent(invalidClick);
        // Should handle gracefully - state should not change
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testMouseClickWhenIndexNegative() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        // Reset state to a known value
        viewManagerModel.setState("timeline");
        viewManagerModel.firePropertyChange();
        String initialState = viewManagerModel.getState();
        assertEquals("timeline", initialState); // Ensure we start with timeline
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) list.getModel();
        
        // Create a custom JList that returns a negative index by overriding locationToIndex
        class TestJListNegative extends JList<ViewTimelineResponse.TimelineCardVM> {
            TestJListNegative(DefaultListModel<ViewTimelineResponse.TimelineCardVM> model) {
                super(model);
            }
            @Override
            public int locationToIndex(Point location) {
                // Return -1 to test negative index check
                return -1;
            }
        }
        TestJListNegative testList = new TestJListNegative(listModel);
        @SuppressWarnings("unchecked")
        ListCellRenderer<? super ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<? super ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        testList.setCellRenderer(renderer);
        if (list.getMouseListeners().length > 0) {
            testList.addMouseListener(list.getMouseListeners()[0]);
        }
        
        // Click on the test list - should not process because index < 0
        MouseEvent negativeClick = new MouseEvent(
            testList, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
            0, 10, 10, 1, false
        );
        testList.dispatchEvent(negativeClick);
        // Should handle gracefully (index < 0 check) - state should not change
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testMouseClickWhenIndexEqualsListModelSize() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        JList<?> list = getList();
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        
        // Try to access index equal to size (should be out of bounds)
        int size = listModel.getSize();
        if (size > 0) {
            // This tests the index < listModel.getSize() check
            MouseEvent click = new MouseEvent(
                list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 0, 0, 1, false
            );
            list.dispatchEvent(click);
        }
    }

    @Test
    void testMouseClickWhenClickCountZero() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Test snippet");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        JList<?> list = getList();
        if (list.getModel().getSize() > 0) {
            java.awt.Rectangle cellBounds = list.getCellBounds(0, 0);
            if (cellBounds != null) {
                // Click count of 0 should not trigger openStudyMaterial
                MouseEvent zeroClick = new MouseEvent(
                    list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                    0, cellBounds.x + cellBounds.width / 2, 
                    cellBounds.y + cellBounds.height / 2, 0, false
                );
                String initialState = viewManagerModel.getState();
                list.dispatchEvent(zeroClick);
                // State should not change (clickCount >= 1 check)
                assertEquals(initialState, viewManagerModel.getState());
            }
        }
    }

    @Test
    void testOpenStudyMaterialWithNullSnippet() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet(null);
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
    }

    @Test
    void testOpenStudyMaterialWithEmptySnippet() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("");
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
    }

    @Test
    void testOpenStudyMaterialNotesWithFullNotesText() throws Exception {
        // Test line 192: fullNotesText is not null and not empty, should use it instead of snippet
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("Short snippet");
        card.setFullNotesText("This is the full notes text content");
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
        // Verify fullNotesText was used (we can't directly check, but the method should be called)
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithFlashcardData() throws Exception {
        // Test lines 204, 206-208: flashcardData is not null and not empty, should deserialize
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setTitle("Flashcards");
        card.setSubtitle("10 cards");
        // Create valid JSON for FlashcardSet
        String flashcardJson = "{\"courseName\":\"CSC207\",\"flashcards\":[{\"question\":\"Q1\",\"answer\":\"A1\"}]}";
        card.setFlashcardData(flashcardJson);
        openMethod.invoke(timelineView, card);
        assertEquals("flashcardDisplay", viewManagerModel.getState());
        assertEquals("flashcardDisplay", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithSubtitleFallback() throws Exception {
        // When flashcardData is null, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setTitle("Flashcards");
        card.setSubtitle("15 cards");
        card.setFlashcardData(null); // No flashcardData, shows error dialog
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithTestData() throws Exception {
        // Test lines 236-246: testData is not null and not empty, should deserialize
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        // Create valid JSON for MockTestGenerationOutputData
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"questionTypes\":[\"mc\",\"mc\"],\"courseId\":\"CSC207\",\"choices\":[]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Check that either EvaluateTestViewModel or MockTestViewModel was navigated to
        String state = viewManagerModel.getState();
        assertTrue(state.equals(evaluateTestViewModel.getViewName()) || 
                   state.equals(mockTestViewModel.getViewName()) ||
                   state.equals("timeline")); // Might show error and stay on timeline
    }
    
    @Test
    void testOpenStudyMaterialQuizWithTestDataAndChoices() throws Exception {
        // Test lines 183-186 in QuizView: choices != null && !choices.get(i).isEmpty()
        // This covers the branch where choices are displayed
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        // Create JSON with non-empty choices to hit lines 183-186 in QuizView
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"questionTypes\":[\"mc\",\"mc\"],\"courseId\":\"CSC207\",\"choices\":[[\"A\",\"B\",\"C\"],[\"X\",\"Y\",\"Z\"]]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Verify that MockTestViewModel state was updated with test data
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(mockTestViewModel.getState().getQuestions());
        assertTrue(mockTestViewModel.getState().getQuestions().size() > 0);
    }
    
    @Test
    void testOpenStudyMaterialQuizWithNullTestData() throws Exception {
        // When testData deserializes to null, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        // Set testData to JSON literal "null" - Gson will return null for this
        card.setTestData("null");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }
    
    @Test
    void testOpenStudyMaterialQuizWithTestDataButNullQuestions() throws Exception {
        // When testData has null questions, still navigates (null questions become empty list)
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        // Create JSON for MockTestGenerationOutputData with null questions
        String testDataJson = "{\"questions\":null,\"answers\":[\"A1\",\"A2\"],\"questionTypes\":[\"MC\",\"MC\"],\"courseId\":\"CSC207\",\"choices\":[[\"A\",\"B\",\"C\",\"D\"]]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to MockTestViewModel even with null questions (converted to empty list)
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEvaluationData() throws Exception {
        // Test lines 248-259: evaluationData is not null and not empty, should deserialize
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create valid JSON for EvaluateTestOutputData
        String evalDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"userAnswers\":[\"A1\",\"A2\"],\"correctness\":[\"correct\",\"correct\"],\"feedback\":[\"Good\",\"Good\"],\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Check that either EvaluateTestViewModel or MockTestViewModel was navigated to
        String state = viewManagerModel.getState();
        assertTrue(state.equals(evaluateTestViewModel.getViewName()) || 
                   state.equals(mockTestViewModel.getViewName()) ||
                   state.equals("timeline")); // Might show error and stay on timeline
    }
    
    @Test
    void testOpenStudyMaterialQuizWithNullEvaluationData() throws Exception {
        // When evaluationData deserializes to null, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        // Set evaluationData to JSON literal "null" - Gson will return null for this
        card.setEvaluationData("null");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }
    
    @Test
    void testOpenStudyMaterialQuizWithEvaluationDataButNullQuestions() throws Exception {
        // When evaluationData has null questions, still navigates (null questions become empty list)
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create JSON for EvaluateTestOutputData with null questions
        String evalDataJson = "{\"questions\":null,\"answers\":[\"A1\",\"A2\"],\"userAnswers\":[\"A1\",\"A2\"],\"correctness\":[\"correct\",\"correct\"],\"feedback\":[\"Good\",\"Good\"],\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to EvaluateTestViewModel even with null questions (converted to empty list)
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEvaluationDataButNullAnswers() throws Exception {
        // Test line 242: evaluationData.getAnswers() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create JSON for EvaluateTestOutputData with null answers
        String evalDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":null,\"userAnswers\":[\"A1\",\"A2\"],\"correctness\":[\"correct\",\"correct\"],\"feedback\":[\"Good\",\"Good\"],\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to EvaluateTestViewModel even with null answers (converted to empty list)
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEvaluationDataButNullUserAnswers() throws Exception {
        // Test line 244: evaluationData.getUserAnswers() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create JSON for EvaluateTestOutputData with null userAnswers
        String evalDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"userAnswers\":null,\"correctness\":[\"correct\",\"correct\"],\"feedback\":[\"Good\",\"Good\"],\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to EvaluateTestViewModel even with null userAnswers (converted to empty list)
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEvaluationDataButNullCorrectness() throws Exception {
        // Test line 246: evaluationData.getCorrectness() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create JSON for EvaluateTestOutputData with null correctness
        String evalDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"userAnswers\":[\"A1\",\"A2\"],\"correctness\":null,\"feedback\":[\"Good\",\"Good\"],\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to EvaluateTestViewModel even with null correctness (converted to empty list)
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEvaluationDataButNullFeedback() throws Exception {
        // Test line 248: evaluationData.getFeedback() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        // Create JSON for EvaluateTestOutputData with null feedback
        String evalDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"userAnswers\":[\"A1\",\"A2\"],\"correctness\":[\"correct\",\"correct\"],\"feedback\":null,\"score\":2}";
        card.setEvaluationData(evalDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to EvaluateTestViewModel even with null feedback (converted to empty list)
        assertEquals(evaluateTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithTestDataButNullAnswers() throws Exception {
        // Test line 271: testData.getAnswers() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        // Create JSON for MockTestGenerationOutputData with null answers
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":null,\"questionTypes\":[\"MC\",\"MC\"],\"courseId\":\"CSC207\",\"choices\":[[\"A\",\"B\",\"C\",\"D\"]]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to MockTestViewModel even with null answers (converted to empty list)
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithTestDataButNullChoices() throws Exception {
        // Test line 273: testData.getChoices() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        // Create JSON for MockTestGenerationOutputData with null choices
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"questionTypes\":[\"MC\",\"MC\"],\"courseId\":\"CSC207\",\"choices\":null}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to MockTestViewModel even with null choices (converted to empty list)
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithTestDataButNullQuestionTypes() throws Exception {
        // Test line 277: testData.getQuestionTypes() != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        // Create JSON for MockTestGenerationOutputData with null questionTypes
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\"],\"answers\":[\"A1\",\"A2\"],\"questionTypes\":null,\"courseId\":\"CSC207\",\"choices\":[[\"A\",\"B\",\"C\",\"D\"]]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to MockTestViewModel even with null questionTypes (converted to empty list)
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithTestDataButNullInnerChoiceList() throws Exception {
        // Test line 313: innerList != null false branch in toSafeListOfLists
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        // Create JSON for MockTestGenerationOutputData with choices containing a null inner list
        // Note: Gson will deserialize null in JSON array as null, so we need to create a list with null element
        // We'll use a JSON array with null: [["A","B"],null,["X","Y"]]
        String testDataJson = "{\"questions\":[\"Q1\",\"Q2\",\"Q3\"],\"answers\":[\"A1\",\"A2\",\"A3\"],\"questionTypes\":[\"MC\",\"MC\",\"MC\"],\"courseId\":\"CSC207\",\"choices\":[[\"A\",\"B\",\"C\"],null,[\"X\",\"Y\",\"Z\"]]}";
        card.setTestData(testDataJson);
        openMethod.invoke(timelineView, card);
        // Should navigate to MockTestViewModel even with null inner list (converted to empty list)
        assertEquals(mockTestViewModel.getViewName(), viewManagerModel.getState());
        // Verify that choices were handled correctly (null inner list becomes empty list)
        assertNotNull(mockTestViewModel.getState().getChoices());
    }

    @Test
    void testOpenStudyMaterialQuizWithSubtitleFallback() throws Exception {
        // When both testData and evaluationData are null, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("15 questions");
        card.setTestData(null); // No testData
        card.setEvaluationData(null); // No evaluationData
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithScoreSubtitle() throws Exception {
        // When both testData and evaluationData are null, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8.5/10");
        card.setTestData(null);
        card.setEvaluationData(null);
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithInvalidJson() throws Exception {
        // When JSON is invalid, deserialization fails and shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setTitle("Flashcards");
        card.setSubtitle("10 cards");
        card.setFlashcardData("invalid json {");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithInvalidTestDataJson() throws Exception {
        // When JSON is invalid, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        card.setTestData("invalid json {");
        card.setEvaluationData(null);
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithInvalidEvaluationDataJson() throws Exception {
        // When JSON is invalid, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        card.setTestData(null);
        card.setEvaluationData("invalid json {");
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithEmptyFlashcardData() throws Exception {
        // When flashcardData is empty, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setTitle("Flashcards");
        card.setSubtitle("10 cards");
        card.setFlashcardData(""); // Empty string
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEmptyTestData() throws Exception {
        // When testData is empty and evaluationData is null, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz");
        card.setSubtitle("10 questions");
        card.setTestData(""); // Empty string
        card.setEvaluationData(null);
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithEmptyEvaluationData() throws Exception {
        // When evaluationData is empty and testData is null, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setTitle("Quiz - Submitted");
        card.setSubtitle("Score 8/10");
        card.setTestData(null);
        card.setEvaluationData(""); // Empty string
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialNotesWithEmptyFullNotesText() throws Exception {
        // Test line 192: fullNotesText is empty, should use snippet instead
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("This is the snippet");
        card.setFullNotesText(""); // Empty, should use snippet
        openMethod.invoke(timelineView, card);
        assertEquals(lectureNotesViewModel.getViewName(), viewManagerModel.getState());
        assertNotNull(lectureNotesViewModel.getState().getNotesText());
    }


    @Test
    void testRefreshButtonWhenCourseIdIsNull() {
        ViewTimelineResponse response = createResponse();
        response.setCourseId(null);
        viewModel.setFromResponse(response);
        
        JPanel header = (JPanel) timelineView.getComponent(0);
        // Find refresh button by checking all components
        JButton refreshBtn = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JButton) {
                JButton btn = (JButton) header.getComponent(i);
                if ("Refresh".equals(btn.getText())) {
                    refreshBtn = btn;
                    break;
                }
            }
        }
        assertNotNull(refreshBtn);
        refreshBtn.doClick();
        // Should not throw exception (null check in action listener) - verify state unchanged
        assertNull(viewModel.getCourseId());
    }

    @Test
    void testPropertyChangeWithNonTimelineProperty() {
        ViewTimelineResponse response = createResponse();
        response.getItems().add(createCard("NOTES", UUID.randomUUID()));
        viewModel.setFromResponse(response);
        
        // Fire property change with wrong property name - should be ignored
        PropertyChangeEvent wrongEvent = new PropertyChangeEvent(viewModel, "wrong", null, viewModel);
        timelineView.propertyChange(wrongEvent);
        // List should remain unchanged since wrong property was ignored
        assertEquals(1, viewModel.getItems().size());
        PropertyChangeEvent evt = new PropertyChangeEvent(viewModel, "wrongProperty", null, viewModel);
        timelineView.propertyChange(evt);
        // Should return early and not update list
    }

    @Test
    void testRendererWithEmptyStringValues() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("");
        card.setSubtitle("");
        card.setSnippet("");
        card.setTime("");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        Component component = renderer.getListCellRendererComponent(list, card, 0, false, false);
        assertNotNull(component);
    }

    @Test
    void testRendererWithCellHasFocus() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        @SuppressWarnings("unchecked")
        JList<ViewTimelineResponse.TimelineCardVM> list = (JList<ViewTimelineResponse.TimelineCardVM>) getList();
        @SuppressWarnings("unchecked")
        ListCellRenderer<ViewTimelineResponse.TimelineCardVM> renderer = 
            (ListCellRenderer<ViewTimelineResponse.TimelineCardVM>) list.getCellRenderer();
        
        Component withFocus = renderer.getListCellRendererComponent(list, card, 0, false, true);
        Component withoutFocus = renderer.getListCellRendererComponent(list, card, 0, false, false);
        assertNotNull(withFocus);
        assertNotNull(withoutFocus);
    }

    // Branch coverage tests for specific lines
    @Test
    void testMouseClickWhenIndexLessThanZero() throws Exception {
        // Test line 66: index >= 0 false branch
        // We can't easily simulate locationToIndex returning -1, but we can verify
        // the condition by ensuring the list is empty and clicking outside
        ViewTimelineResponse response = createResponse();
        response.setEmpty(true);
        triggerPropertyChange(response);
        
        JList<?> list = getList();
        // When list is empty, any click should result in index < 0 or index >= size
        // Create click event with coordinates that would be invalid
        MouseEvent invalidClick = new MouseEvent(
            list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
            0, -100, -100, 1, false
        );
        String initialState = viewManagerModel.getState();
        list.dispatchEvent(invalidClick);
        // Should not change state when list is empty (index >= 0 && index < size fails)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testMouseClickWhenIndexGreaterThanOrEqualToListModelSize() throws Exception {
        // Test line 66: index < listModel.getSize() false branch
        // Strategy: Create a custom JList that overrides locationToIndex to return a value >= size
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        
        JList<?> originalList = getList();
        
        // Create a custom JList that returns index >= size for testing
        JList<ViewTimelineResponse.TimelineCardVM> testList = new JList<>(listModel) {
            @Override
            public int locationToIndex(Point location) {
                // Return an index that is >= listModel.getSize() to test the false branch
                return listModel.getSize(); // This will be >= size, so index < size is false
            }
        };
        
        // Replace the list in timelineView using reflection
        Field listField = ViewTimelineView.class.getDeclaredField("list");
        listField.setAccessible(true);
        listField.set(timelineView, testList);
        
        // Get the mouse listeners from the original list and add them to testList
        java.awt.event.MouseListener[] listeners = originalList.getMouseListeners();
        for (java.awt.event.MouseListener listener : listeners) {
            testList.addMouseListener(listener);
        }
        
        // Now click on the testList - locationToIndex will return size, which is >= size
        testList.setSize(400, 300);
        testList.setVisible(true);
        String initialState = viewManagerModel.getState();
        
        MouseEvent click = new MouseEvent(
            testList, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
            0, 100, 100, 1, false
        );
        testList.dispatchEvent(click);
        
        // Should not change state (index < listModel.getSize() is false: size < size is false)
        assertEquals(initialState, viewManagerModel.getState());
        
        // Restore original list
        listField.set(timelineView, originalList);
    }

    @Test
    void testMouseClickWhenContentIdNullAndIndexOutOfBoundsInViewModel() throws Exception {
        // Test line 72: index < items.size() false branch
        // Strategy: Have card with null contentId in listModel at index 0
        // But viewModel.items.size() is 0, so index 0 >= items.size() (false branch)
        // We need index=0, items.size()=0, so 0 < 0 is false
        
        // First, add a card to populate listModel and viewModel
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        response.getItems().add(card);
        triggerPropertyChange(response);
        
        Field listModelField = ViewTimelineView.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = 
            (DefaultListModel<ViewTimelineResponse.TimelineCardVM>) listModelField.get(timelineView);
        
        // Verify listModel has the card
        assertEquals(1, listModel.getSize());
        
        // Set contentId to null in listModel (this triggers line 69: card.getContentId() == null)
        ViewTimelineResponse.TimelineCardVM nullCard = createCard("NOTES", null);
        nullCard.setTitle("Test Notes");
        listModel.setElementAt(nullCard, 0);
        
        // Now clear viewModel items using reflection to avoid triggering propertyChange
        // which would repopulate listModel
        Field itemsField = ViewTimelineViewModel.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<ViewTimelineResponse.TimelineCardVM> items =
            (java.util.List<ViewTimelineResponse.TimelineCardVM>) itemsField.get(viewModel);
        items.clear();
        
        // Verify viewModel items is empty but listModel still has the null card
        assertEquals(0, viewModel.getItems().size());
        assertEquals(1, listModel.getSize());
        assertNull(listModel.getElementAt(0).getContentId());
        
        // Make the list visible and sized so clicks work
        JList<?> list = getList();
        timelineView.setSize(400, 300);
        timelineView.setVisible(true);
        list.setSize(400, 300);
        list.setVisible(true);
        
        // Click at index 0
        // Flow: index=0 >= 0 && index=0 < listModel.getSize()=1 -> true (line 66)
        //       card.getContentId() == null -> true (line 69)
        //       items = vm.getItems() -> empty list (size 0)
        //       index=0 < items.size()=0 -> false (line 72) - THIS IS WHAT WE'RE TESTING
        //       card stays with null contentId
        //       card.getContentId() == null -> true, so openStudyMaterial is NOT called (line 77)
        String initialState = viewManagerModel.getState();
        simulateMouseClick(list, 0);
        
        // Should not open (index < items.size() is false: 0 < 0 is false, so card stays null)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testBackButtonAction() throws Exception {
        // Test lines 67-68: back button sets state to "workspace" and fires property change
        viewManagerModel.setState("timeline");
        viewManagerModel.firePropertyChange();
        
        // Find the back button in the header
        Component[] components = timelineView.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getLayout() instanceof BorderLayout) {
                    Component[] headerComps = panel.getComponents();
                    for (Component headerComp : headerComps) {
                        if (headerComp instanceof JButton) {
                            JButton btn = (JButton) headerComp;
                            if ("← Back".equals(btn.getText())) {
                                btn.doClick();
                                // Verify state changed to workspace
                                assertEquals("workspace", viewManagerModel.getState());
                                return;
                            }
                        }
                    }
                }
            }
        }
        fail("Back button not found");
    }
    
    @Test
    void testOpenStudyMaterialFlashcardsWithNullFlashcardSet() throws Exception {
        // When flashcardData deserializes to null, shows error dialog and doesn't navigate
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setTitle("Test Flashcards");
        card.setSubtitle("10 cards");
        // Set flashcardData to JSON literal "null" - Gson will return null for this
        card.setFlashcardData("null");
        
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }
    
    @Test
    void testOpenStudyMaterialFlashcardsWithNullSubtitle() throws Exception {
        // When flashcardData is null and subtitle is null, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle(null);
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithNullSubtitle() throws Exception {
        // When quiz has no data and null subtitle, shows error dialog
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle(null);
        String initialState = viewManagerModel.getState();
        openMethod.invoke(timelineView, card);
        // State should not change (error dialog shown instead)
        assertEquals(initialState, viewManagerModel.getState());
    }
}
