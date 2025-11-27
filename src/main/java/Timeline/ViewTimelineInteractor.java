package Timeline;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ViewTimelineInteractor implements ViewTimelineInputBoundary {
    private final ITimelineRepository timelineRepo;
    private final ViewTimelineOutputBoundary presenter;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, HH:mm");

    public ViewTimelineInteractor(ITimelineRepository t, ViewTimelineOutputBoundary p) {
        this.timelineRepo = t;
        this.presenter = p;
    }

    @Override
    public void execute(UUID courseId) {
        List<TimelineEvent> events = timelineRepo.findByCourseNewestFirst(courseId);

        if (events.isEmpty()) {
            presenter.presentNotFound("This page is empty");
            return;
        }

        ViewTimelineResponse resp = new ViewTimelineResponse();
        resp.setCourseId(courseId);
        resp.setItems(events.stream().map(e -> toCard(e, resp)).toList());
        resp.setEmpty(resp.getItems().isEmpty());

        presenter.present(resp);
    }

    private ViewTimelineResponse.TimelineCardVM toCard(TimelineEvent e, ViewTimelineResponse response) {
        ViewTimelineResponse.TimelineCardVM vm = response.new TimelineCardVM();
        vm.setTime(FMT.format(e.getOccurredAt().atZone(ZoneId.systemDefault())));
        vm.setContentId(e.getContentId());
        vm.setEventId(e.getId().toString());

        switch (e.getType()) {
            case NOTES_GENERATED:
                vm.setIcon("notes");
                vm.setType("NOTES");
                vm.setTitle(e.getTitle() == null || e.getTitle().isEmpty() ? "Notes" : e.getTitle());
                vm.setSnippet(e.getSnippet());
                break;
            case FLASHCARDS_GENERATED:
                vm.setIcon("cards");
                vm.setType("FLASHCARDS");
                vm.setTitle("Flashcards");
                vm.setSubtitle(e.getNumCards() == null ? "" : e.getNumCards() + " cards");
                break;
            case QUIZ_GENERATED:
                vm.setIcon("quiz");
                vm.setType("QUIZ");
                vm.setTitle("Quiz");
                vm.setSubtitle(e.getNumQuestions() == null ? "" : e.getNumQuestions() + " questions");
                break;
            case QUIZ_SUBMITTED:
                vm.setIcon("score");
                vm.setType("QUIZ");
                vm.setTitle("Quiz — Submitted");
                if (e.getNumQuestions() != null && e.getScore() != null) {
                    vm.setSubtitle("Score " + e.getScore() + "/" + e.getNumQuestions());
                } else {
                    vm.setSubtitle("");
                }
                break;
        }
        return vm;
    }
}
