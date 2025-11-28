package Timeline;

import com.google.gson.Gson;
import entities.FlashcardSet;
import usecases.mock_test_generation.MockTestGenerationOutputData;
import usecases.evaluate_test.EvaluateTestOutputData;

import java.util.UUID;

public class TimelineLogger {
    private final ITimelineRepository repo;
    private final Gson gson = new Gson();

    public TimelineLogger(ITimelineRepository repo) {
        this.repo = repo;
    }

    public void logNotesGenerated(UUID courseId, UUID contentId, String title, String snippet, String fullNotesText) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        e.setTitle(title);
        e.setSnippet(snippet);
        e.setFullNotesText(fullNotesText);
        repo.save(e);
    }

    public void logFlashcardsGenerated(UUID courseId, UUID contentId, int numCards, FlashcardSet flashcardSet) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        e.setNumCards(numCards);
        if (flashcardSet != null) {
            e.setFlashcardData(gson.toJson(flashcardSet));
        }
        repo.save(e);
    }

    public void logQuizGenerated(UUID courseId, UUID contentId, int numQuestions, MockTestGenerationOutputData testData) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        e.setNumQuestions(numQuestions);
        if (testData != null) {
            e.setTestData(gson.toJson(testData));
        }
        repo.save(e);
    }

    public void logQuizSubmitted(UUID courseId, UUID contentId, int numQuestions, double score, EvaluateTestOutputData evaluationData) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        e.setNumQuestions(numQuestions);
        e.setScore(score);
        if (evaluationData != null) {
            e.setEvaluationData(gson.toJson(evaluationData));
        }
        repo.save(e);
    }
}
