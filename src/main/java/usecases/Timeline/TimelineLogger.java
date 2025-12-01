package usecases.Timeline;

import com.google.gson.Gson;
import entities.FlashcardSet;

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

    public void logQuizGenerated(UUID courseId, UUID contentId, int numQuestions, String testDataJson) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        e.setNumQuestions(numQuestions);
        if (testDataJson != null && !testDataJson.isEmpty()) {
            e.setTestData(testDataJson);
        }
        repo.save(e);
    }

    public void logQuizSubmitted(UUID courseId, UUID contentId, int numQuestions, double score, String evaluationDataJson) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        e.setNumQuestions(numQuestions);
        e.setScore(score);
        if (evaluationDataJson != null && !evaluationDataJson.isEmpty()) {
            e.setEvaluationData(evaluationDataJson);
        }
        repo.save(e);
    }
}
