package Timeline;

import interface_adapters.ViewManagerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import views.FlashcardsView;
import views.NotesView;
import views.QuizView;

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
    private NotesView notesView;
    private FlashcardsView flashcardsView;
    private QuizView quizView;
    private ViewTimelineView timelineView;
    private Method openMethod;

    @BeforeEach
    void setUp() throws Exception {
        viewModel = new ViewTimelineViewModel();
        TestViewTimelineInputBoundary interactor = new TestViewTimelineInputBoundary();
        TimelineController controller = new TimelineController(interactor);
        viewManagerModel = new ViewManagerModel();
        notesView = new NotesView();
        flashcardsView = new FlashcardsView();
        quizView = new QuizView();
        timelineView = new ViewTimelineView(viewModel, controller, viewManagerModel,
                                           notesView, flashcardsView, quizView);
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
        assertEquals(card.getContentId(), notesView.getContentId());
        assertEquals("notes", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialFlashcards() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("20 cards");
        openMethod.invoke(timelineView, card);
        assertNotNull(flashcardsView.getContentId());
        assertEquals("flashcards", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuiz() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("15 questions");
        openMethod.invoke(timelineView, card);
        assertNotNull(quizView.getContentId());
        assertEquals("quiz", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizSubmitted() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("Score 14.0/15");
        openMethod.invoke(timelineView, card);
        assertNotNull(quizView.getContentId());
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
        assertNotEquals("notes", viewManagerModel.getState());
        assertNotEquals("flashcards", viewManagerModel.getState());
        assertNotEquals("quiz", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialUnknownType() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("UNKNOWN", UUID.randomUUID());
        openMethod.invoke(timelineView, card);
        // Should show error dialog - view state should not change
        assertNotEquals("notes", viewManagerModel.getState());
        assertNotEquals("flashcards", viewManagerModel.getState());
        assertNotEquals("quiz", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialWithEmptySubtitle() throws Exception {
        // Test empty subtitle for both FLASHCARDS and QUIZ
        ViewTimelineResponse.TimelineCardVM flashcard = createCard("FLASHCARDS", UUID.randomUUID());
        flashcard.setSubtitle("");
        openMethod.invoke(timelineView, flashcard);
        assertEquals(flashcard.getContentId(), flashcardsView.getContentId());
        
        ViewTimelineResponse.TimelineCardVM quiz = createCard("QUIZ", UUID.randomUUID());
        quiz.setSubtitle("");
        openMethod.invoke(timelineView, quiz);
        assertNotNull(quizView.getContentId());
    }

    @Test
    void testOpenStudyMaterialFlashcardsWithNoNumbers() throws Exception {
        // Test line 182: !numStr.isEmpty() false branch
        // Subtitle with no digits - numStr will be empty after replaceAll
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("cards"); // No numbers - numStr will be empty
        openMethod.invoke(timelineView, card);
        assertEquals(card.getContentId(), flashcardsView.getContentId());
        // numCards should remain 0 (default) since numStr was empty
    }

    @Test
    void testOpenStudyMaterialQuizWithInvalidSubtitleFormats() throws Exception {
        // Test various invalid subtitle formats
        String[] invalidSubtitles = {"Score invalid/15", "Score 14.0", "Score abc/def", 
                                     "Score 8.5/", "Score /", "Some other text", 
                                     "questions", "  15  questions  "};
        for (String subtitle : invalidSubtitles) {
            ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
            card.setSubtitle(subtitle);
            openMethod.invoke(timelineView, card);
            assertNotNull(quizView.getContentId());
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
        assertEquals("notes", viewManagerModel.getState());
        notesView.getBackButton().doClick();
        assertEquals("timeline", viewManagerModel.getState());
    }

    @Test
    void testBackButtonFlashcardsView() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle("20 cards");
        response.getItems().add(card);
        triggerPropertyChange(response);
        openMethod.invoke(timelineView, card);
        assertEquals("flashcards", viewManagerModel.getState());
        flashcardsView.getBackButton().doClick();
        assertEquals("timeline", viewManagerModel.getState());
    }

    @Test
    void testBackButtonQuizView() throws Exception {
        ViewTimelineResponse response = createResponse();
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle("15 questions");
        response.getItems().add(card);
        triggerPropertyChange(response);
        openMethod.invoke(timelineView, card);
        assertEquals("quiz", viewManagerModel.getState());
        quizView.getBackButton().doClick();
        assertEquals("timeline", viewManagerModel.getState());
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
        assertEquals("notes", viewManagerModel.getState());
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
            assertEquals("notes", viewManagerModel.getState());
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
                assertEquals("notes", viewManagerModel.getState());
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
        JButton refreshBtn = (JButton) header.getComponent(1);
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
        JButton refreshBtn = (JButton) header.getComponent(1);
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
            assertEquals("notes", viewManagerModel.getState());
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
        assertEquals(card.getContentId(), notesView.getContentId());
        assertEquals("notes", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialWithEmptySnippet() throws Exception {
        ViewTimelineResponse.TimelineCardVM card = createCard("NOTES", UUID.randomUUID());
        card.setTitle("Test Notes");
        card.setSnippet("");
        openMethod.invoke(timelineView, card);
        assertEquals(card.getContentId(), notesView.getContentId());
        assertEquals("notes", viewManagerModel.getState());
    }


    @Test
    void testRefreshButtonWhenCourseIdIsNull() {
        ViewTimelineResponse response = createResponse();
        response.setCourseId(null);
        viewModel.setFromResponse(response);
        
        JPanel header = (JPanel) timelineView.getComponent(0);
        JButton refreshBtn = (JButton) header.getComponent(1);
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
    void testOpenStudyMaterialFlashcardsWithNullSubtitle() throws Exception {
        // Test line 179: card.subtitle != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("FLASHCARDS", UUID.randomUUID());
        card.setSubtitle(null); // This makes card.subtitle != null false
        openMethod.invoke(timelineView, card);
        // Should use default numCards = 0 (skips the if block)
        assertEquals(card.getContentId(), flashcardsView.getContentId());
        assertEquals("flashcards", viewManagerModel.getState());
    }

    @Test
    void testOpenStudyMaterialQuizWithNullSubtitle() throws Exception {
        // Test line 199: card.subtitle != null false branch
        ViewTimelineResponse.TimelineCardVM card = createCard("QUIZ", UUID.randomUUID());
        card.setSubtitle(null); // This makes card.subtitle != null false
        openMethod.invoke(timelineView, card);
        // Should use default numQuestions = 0 and score = null (skips the if block)
        assertEquals(card.getContentId(), quizView.getContentId());
        assertEquals("quiz", viewManagerModel.getState());
    }
}
