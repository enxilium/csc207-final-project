package Timeline;

import usecases.Timeline.ViewTimelineOutputBoundary;
import usecases.Timeline.ViewTimelineResponse;

/**
 * Simple test implementation of ViewTimelineOutputBoundary for testing purposes.
 */
public class TestViewTimelineOutputBoundary implements ViewTimelineOutputBoundary {
    private ViewTimelineResponse lastResponse;
    private String lastNotFoundMessage;
    private int presentNotFoundCount = 0;

    @Override
    public void present(ViewTimelineResponse response) {
        this.lastResponse = response;
    }

    @Override
    public void presentNotFound(String message) {
        this.lastNotFoundMessage = message;
        presentNotFoundCount++;
    }

    public ViewTimelineResponse getLastResponse() {
        return lastResponse;
    }

    public String getLastNotFoundMessage() {
        return lastNotFoundMessage;
    }

    public int getPresentNotFoundCount() {
        return presentNotFoundCount;
    }
}


