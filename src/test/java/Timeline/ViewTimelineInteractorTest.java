package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ViewTimelineInteractorTest {
    private TestTimelineRepository repository;
    private TestViewTimelineOutputBoundary presenter;
    private ViewTimelineInteractor interactor;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        repository = new TestTimelineRepository();
        presenter = new TestViewTimelineOutputBoundary();
        interactor = new ViewTimelineInteractor(repository, presenter);
        courseId = UUID.randomUUID();
    }

    @Test
    void testExecuteWithEmptyCourse() {
        interactor.execute(courseId);

        assertEquals(1, presenter.getPresentNotFoundCount());
        assertEquals("This page is empty", presenter.getLastNotFoundMessage());
    }

    @Test
    void testExecuteWithNotesEvent() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Test Notes");
        event.setSnippet("Test snippet");
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        assertNotNull(response);
        assertEquals(courseId, response.getCourseId());
        assertEquals(1, response.getItems().size());

        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("NOTES", card.getType());
        assertEquals("notes", card.getIcon());
        assertEquals("Test Notes", card.getTitle());
        assertEquals("Test snippet", card.getSnippet());
        assertEquals(contentId, card.getContentId());
    }

    @Test
    void testExecuteWithFlashcardsEvent() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        event.setNumCards(20);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("FLASHCARDS", card.getType());
        assertEquals("cards", card.getIcon());
        assertEquals("Flashcards", card.getTitle());
        assertEquals("20 cards", card.getSubtitle());
    }

    @Test
    void testExecuteWithQuizGeneratedEvent() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        event.setNumQuestions(15);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("QUIZ", card.getType());
        assertEquals("quiz", card.getIcon());
        assertEquals("Quiz", card.getTitle());
        assertEquals("15 questions", card.getSubtitle());
    }

    @Test
    void testExecuteWithQuizSubmittedEvent() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        event.setNumQuestions(10);
        event.setScore(8.5);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("QUIZ", card.getType());
        assertEquals("score", card.getIcon());
        assertEquals("Quiz — Submitted", card.getTitle());
        assertEquals("Score 8.5/10", card.getSubtitle());
    }

    @Test
    void testExecuteWithNullValues() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        // Don't set numCards - should handle null
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("", card.getSubtitle());
    }

    @Test
    void testExecuteWithQuizGeneratedEventNullNumQuestions() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        // Don't set numQuestions - should handle null
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("", card.getSubtitle());
    }

    @Test
    void testExecuteWithMultipleEvents() {
        UUID notesId = UUID.randomUUID();
        UUID cardsId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        TimelineEvent notes = new TimelineEvent(courseId, notesId, TimelineEventType.NOTES_GENERATED);
        TimelineEvent cards = new TimelineEvent(courseId, cardsId, TimelineEventType.FLASHCARDS_GENERATED);
        TimelineEvent quiz = new TimelineEvent(courseId, quizId, TimelineEventType.QUIZ_GENERATED);

        repository.save(notes);
        repository.save(cards);
        repository.save(quiz);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        assertEquals(3, response.getItems().size());
        // Newest first: quiz, cards, notes
        assertEquals("QUIZ", response.getItems().get(0).getType());
        assertEquals("FLASHCARDS", response.getItems().get(1).getType());
        assertEquals("NOTES", response.getItems().get(2).getType());
    }

    @Test
    void testNotesTitleNullDefaultsToNotes() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        // Don't set title
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("Notes", card.getTitle());
    }

    @Test
    void testNotesTitleEmptyDefaultsToNotes() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle(""); // Empty string
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("Notes", card.getTitle());
    }


    @Test
    void testQuizSubmittedNullScore() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        event.setNumQuestions(10);
        // Don't set score - should go to else branch
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("", card.getSubtitle());
    }

    @Test
    void testQuizSubmittedNullNumQuestions() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        event.setScore(8.5);
        // Don't set numQuestions - should go to else branch
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("", card.getSubtitle());
    }

    @Test
    void testQuizSubmittedBothNull() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        // Don't set score or numQuestions - should go to else branch
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals("", card.getSubtitle());
    }

    @Test
    void testResponseEmptyWhenItemsEmpty() {
        // This shouldn't happen in practice, but test the branch
        ViewTimelineResponse response = new ViewTimelineResponse();
        response.setCourseId(courseId);
        response.setItems(new ArrayList<>());
        response.setEmpty(response.getItems().isEmpty());
        
        assertTrue(response.isEmpty());
    }

    @Test
    void testAllEventTypes() {
        UUID notesId = UUID.randomUUID();
        UUID cardsId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();
        UUID subId = UUID.randomUUID();

        TimelineEvent notes = new TimelineEvent(courseId, notesId, TimelineEventType.NOTES_GENERATED);
        notes.setTitle("Test");
        notes.setSnippet("Snippet");
        TimelineEvent cards = new TimelineEvent(courseId, cardsId, TimelineEventType.FLASHCARDS_GENERATED);
        cards.setNumCards(5);
        TimelineEvent quiz = new TimelineEvent(courseId, quizId, TimelineEventType.QUIZ_GENERATED);
        quiz.setNumQuestions(3);
        TimelineEvent submitted = new TimelineEvent(courseId, subId, TimelineEventType.QUIZ_SUBMITTED);
        submitted.setNumQuestions(3);
        submitted.setScore(2.5);

        repository.save(notes);
        repository.save(cards);
        repository.save(quiz);
        repository.save(submitted);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        assertEquals(4, response.getItems().size());
        
        // Verify all types are present
        List<String> types = response.getItems().stream()
                .map(ViewTimelineResponse.TimelineCardVM::getType)
                .collect(Collectors.toList());
        assertTrue(types.contains("NOTES"));
        assertTrue(types.contains("FLASHCARDS"));
        assertTrue(types.contains("QUIZ"));
    }

    @Test
    void testExecuteSetsResponseEmptyCorrectly() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        assertFalse(response.isEmpty()); // Should be false when items exist
    }

    @Test
    void testToCardSetsEventId() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertNotNull(card.getEventId());
        assertEquals(event.getId().toString(), card.getEventId());
    }

    @Test
    void testToCardSetsTime() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertNotNull(card.getTime());
        assertFalse(card.getTime().isEmpty());
    }

    @Test
    void testToCardSetsContentId() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertEquals(contentId, card.getContentId());
    }

    @Test
    void testNotesWithNullSnippet() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Test");
        // Don't set snippet - should be null
        repository.save(event);

        interactor.execute(courseId);

        ViewTimelineResponse response = presenter.getLastResponse();
        ViewTimelineResponse.TimelineCardVM card = response.getItems().get(0);
        assertNull(card.getSnippet());
    }
}

