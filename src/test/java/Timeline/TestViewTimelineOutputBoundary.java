package Timeline;

import usecases.Timeline.ViewTimelineOutputBoundary;
import usecases.Timeline.ViewTimelineResponse;

public class TestViewTimelineOutputBoundary implements ViewTimelineOutputBoundary {
    private ViewTimelineResponse lastResponse;
    private int presentNotFoundCount = 0;
    private String lastNotFoundMessage;

    @Override
    public void present(ViewTimelineResponse response) {
        this.lastResponse = response;
    }

    @Override
    public void presentNotFound(String message) {
        this.presentNotFoundCount++;
        this.lastNotFoundMessage = message;
    }

    public ViewTimelineResponse getLastResponse() {
        return lastResponse;
    }

    public int getPresentNotFoundCount() {
        return presentNotFoundCount;
    }

    public String getLastNotFoundMessage() {
        return lastNotFoundMessage;
    }
}

