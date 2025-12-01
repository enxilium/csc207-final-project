package interface_adapters.timeline;

import usecases.Timeline.ViewTimelineResponse;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTimelineViewModel {
    public static final String VIEW_NAME = "timeline";
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private UUID courseId;
    private boolean isEmpty = true;
    private List<ViewTimelineResponse.TimelineCardVM> items = new ArrayList<>();

    public void setFromResponse(ViewTimelineResponse resp) {
        this.courseId = resp.getCourseId();
        this.isEmpty = resp.isEmpty();
        this.items = new ArrayList<>(resp.getItems()); // Defensive copy
        pcs.firePropertyChange("timeline", null, this);
    }

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public boolean isEmpty() { return isEmpty; }
    public List<ViewTimelineResponse.TimelineCardVM> getItems() { return new ArrayList<>(items); } // Defensive copy

    public void addPropertyChangeListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public String getViewName() { return VIEW_NAME; }
}

