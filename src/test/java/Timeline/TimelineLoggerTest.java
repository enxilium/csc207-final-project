package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimelineLoggerTest {
    private TestTimelineRepository repository;
    private TimelineLogger logger;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        repository = new TestTimelineRepository();
        logger = new TimelineLogger(repository);
        courseId = UUID.randomUUID();
    }

    @Test
    void testLogNotesGenerated() {
        UUID contentId = UUID.randomUUID();
        logger.logNotesGenerated(courseId, contentId, "Test Notes", "Test snippet", "Full notes text here");

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.NOTES_GENERATED, event.getType());
        assertEquals("Test Notes", event.getTitle());
        assertEquals("Test snippet", event.getSnippet());
        assertEquals("Full notes text here", event.getFullNotesText());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogFlashcardsGenerated() {
        UUID contentId = UUID.randomUUID();
        java.util.ArrayList<entities.Flashcard> flashcards = new java.util.ArrayList<>();
        entities.FlashcardSet flashcardSet = new entities.FlashcardSet("Test Course", flashcards);
        logger.logFlashcardsGenerated(courseId, contentId, 15, flashcardSet);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, event.getType());
        assertEquals(15, event.getNumCards());
        assertEquals(contentId, event.getContentId());
        assertNotNull(event.getFlashcardData()); // Verify flashcardData was serialized (covers line 30)
    }

    @Test
    void testLogFlashcardsGeneratedWithNullFlashcardSet() {
        UUID contentId = UUID.randomUUID();
        logger.logFlashcardsGenerated(courseId, contentId, 15, null);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, event.getType());
        assertEquals(15, event.getNumCards());
        assertEquals(contentId, event.getContentId());
        assertNull(event.getFlashcardData()); // Verify flashcardData is null when flashcardSet is null
    }

    @Test
    void testLogQuizGenerated() {
        UUID contentId = UUID.randomUUID();
        usecases.mock_test_generation.MockTestGenerationOutputData testData = null;
        logger.logQuizGenerated(courseId, contentId, 10, testData);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_GENERATED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogQuizGeneratedWithTestData() {
        UUID contentId = UUID.randomUUID();
        // Create a non-null testData to cover the true branch of line 38
        java.util.List<String> questions = new java.util.ArrayList<>();
        questions.add("Question 1");
        java.util.List<String> answers = new java.util.ArrayList<>();
        answers.add("Answer 1");
        java.util.List<String> questionTypes = new java.util.ArrayList<>();
        questionTypes.add("multiple_choice");
        entities.TestData testDataEntity = new entities.TestData(questions, answers, questionTypes);
        java.util.List<java.util.List<String>> choices = new java.util.ArrayList<>();
        usecases.mock_test_generation.MockTestGenerationOutputData testData = 
            new usecases.mock_test_generation.MockTestGenerationOutputData(testDataEntity, "CSC207", choices);
        
        logger.logQuizGenerated(courseId, contentId, 10, testData);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_GENERATED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(contentId, event.getContentId());
        assertNotNull(event.getTestData()); // Verify testData was serialized
    }

    @Test
    void testLogQuizSubmitted() {
        UUID contentId = UUID.randomUUID();
        usecases.evaluate_test.EvaluateTestOutputData evaluationData = null;
        logger.logQuizSubmitted(courseId, contentId, 10, 8.5, evaluationData);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(8.5, event.getScore());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogQuizSubmittedWithEvaluationData() {
        UUID contentId = UUID.randomUUID();
        // Create a non-null evaluationData to cover the true branch of line 48 and line 49
        java.util.List<String> questions = new java.util.ArrayList<>();
        questions.add("Question 1");
        java.util.List<String> answers = new java.util.ArrayList<>();
        answers.add("Answer 1");
        java.util.List<String> userAnswers = new java.util.ArrayList<>();
        userAnswers.add("User Answer 1");
        java.util.List<String> correctness = new java.util.ArrayList<>();
        correctness.add("correct");
        java.util.List<String> feedback = new java.util.ArrayList<>();
        feedback.add("Good job!");
        entities.EvaluationData evalData = new entities.EvaluationData(questions, answers, userAnswers, correctness, feedback, 1);
        usecases.evaluate_test.EvaluateTestOutputData evaluationData = 
            new usecases.evaluate_test.EvaluateTestOutputData(evalData);
        
        logger.logQuizSubmitted(courseId, contentId, 10, 8.5, evaluationData);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(8.5, event.getScore());
        assertEquals(contentId, event.getContentId());
        assertNotNull(event.getEvaluationData()); // Verify evaluationData was serialized
    }

    @Test
    void testMultipleLogs() {
        UUID notesId = UUID.randomUUID();
        UUID cardsId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        logger.logNotesGenerated(courseId, notesId, "Notes", "Snippet", "Full notes");
        logger.logFlashcardsGenerated(courseId, cardsId, 20, new entities.FlashcardSet("Test Course", new java.util.ArrayList<>()));
        logger.logQuizGenerated(courseId, quizId, 15, null);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(3, events.size());
    }
}


