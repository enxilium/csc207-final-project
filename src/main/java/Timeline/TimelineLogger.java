package Timeline;

import java.util.UUID;

public class TimelineLogger {
    private final ITimelineRepository repo;

    public TimelineLogger(ITimelineRepository repo) {
        this.repo = repo;
    }

    public void logNotesGenerated(UUID courseId, UUID contentId, String title, String snippet) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        e.setTitle(title);
        e.setSnippet(snippet);
        repo.save(e);
    }

    public void logFlashcardsGenerated(UUID courseId, UUID contentId, int numCards) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        e.setNumCards(numCards);
        repo.save(e);
    }

    public void logQuizGenerated(UUID courseId, UUID contentId, int numQuestions) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        e.setNumQuestions(numQuestions);
        repo.save(e);
    }

    public void logQuizSubmitted(UUID courseId, UUID contentId, int numQuestions, double score) {
        var e = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        e.setNumQuestions(numQuestions);
        e.setScore(score);
        repo.save(e);
    }
}
