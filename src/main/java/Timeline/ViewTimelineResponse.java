package Timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTimelineResponse {
    private UUID courseId;
    private boolean isEmpty;
    private List<TimelineCardVM> items = new ArrayList<>();

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }

    public boolean isEmpty() { return isEmpty; }
    public void setEmpty(boolean empty) { isEmpty = empty; }

    public List<TimelineCardVM> getItems() { return items; }
    public void setItems(List<TimelineCardVM> items) { this.items = items; }

    public static class TimelineCardVM {
        public String time;
        public String icon;
        public String type;
        public String title;
        public String subtitle;
        public String snippet;
        public java.util.UUID contentId;
        public String eventId;
    }
}
