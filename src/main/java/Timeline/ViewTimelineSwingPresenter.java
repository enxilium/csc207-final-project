package Timeline;

public class ViewTimelineSwingPresenter implements ViewTimelineOutputBoundary {
    private final ViewTimelineViewModel vm;

    public ViewTimelineSwingPresenter(ViewTimelineViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void present(ViewTimelineResponse response) {
        vm.setFromResponse(response);
    }

    @Override
    public void presentNotFound(String message) {
        ViewTimelineResponse resp = new ViewTimelineResponse();
        resp.setEmpty(true);

        ViewTimelineResponse.TimelineCardVM card = new ViewTimelineResponse.TimelineCardVM();
        card.title = "Info";
        card.subtitle = message;
        resp.getItems().add(card);

        vm.setFromResponse(resp);
    }
}
