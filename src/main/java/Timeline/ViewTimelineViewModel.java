package Timeline;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTimelineViewModel {
    public static final String VIEW_NAME = "timeline";
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private UUID courseId;
    private boolean isEmpty;
    private List<ViewTimelineResponse.TimelineCardVM> items = new ArrayList<>();

    public void setFromResponse(ViewTimelineResponse resp) {
        this.courseId = resp.getCourseId();
        this.isEmpty = resp.isEmpty();
        this.items = new ArrayList<>(resp.getItems());
        pcs.firePropertyChange("timeline", null, this);
    }

    public UUID getCourseId() { return courseId; }
    public boolean isEmpty() { return isEmpty; }
    public List<ViewTimelineResponse.TimelineCardVM> getItems() { return items; }

    public void addPropertyChangeListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public String getViewName() { return VIEW_NAME; }
}
