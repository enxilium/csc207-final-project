package Timeline;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        resp.setItems(events.stream().map(this::toCard).collect(Collectors.toList()));
        resp.setEmpty(resp.getItems().isEmpty());

        presenter.present(resp);
    }

    private ViewTimelineResponse.TimelineCardVM toCard(TimelineEvent e) {
        ViewTimelineResponse.TimelineCardVM vm = new ViewTimelineResponse.TimelineCardVM();
        vm.time = FMT.format(e.getOccurredAt().atZone(ZoneId.systemDefault()));
        vm.contentId = e.getContentId();
        vm.eventId = e.getId().toString();

        switch (e.getType()) {
            case NOTES_GENERATED:
                vm.icon = "notes"; vm.type = "NOTES";
                vm.title = (e.getTitle() == null ? "Notes" : e.getTitle());
                vm.snippet = e.getSnippet();
                break;
            case FLASHCARDS_GENERATED:
                vm.icon = "cards"; vm.type = "FLASHCARDS";
                vm.title = "Flashcards";
                vm.subtitle = (e.getNumCards() == null ? "" : e.getNumCards() + " cards");
                break;
            case QUIZ_GENERATED:
                vm.icon = "quiz"; vm.type = "QUIZ";
                vm.title = "Quiz";
                vm.subtitle = (e.getNumQuestions() == null ? "" : e.getNumQuestions() + " questions");
                break;
            case QUIZ_SUBMITTED:
                vm.icon = "score"; vm.type = "QUIZ";
                vm.title = "Quiz — Submitted";
                if (e.getNumQuestions() != null && e.getScore() != null) {
                    vm.subtitle = "Score " + e.getScore() + "/" + e.getNumQuestions();
                }
                break;
        }
        return vm;
    }
}
