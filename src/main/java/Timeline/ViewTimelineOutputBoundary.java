package Timeline;

public interface ViewTimelineOutputBoundary {
    void present(ViewTimelineResponse response);
    void presentNotFound(String message);
}
