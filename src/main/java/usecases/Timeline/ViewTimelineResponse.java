package usecases.Timeline;

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

    public class TimelineCardVM {
        private String time;
        private String icon;
        private String type;
        private String title;
        private String subtitle;
        private String snippet;
        private UUID contentId;
        private String eventId;
        
        // Full content fields
        private String fullNotesText;
        private String flashcardData;
        private String testData;
        private String evaluationData;

        // Getters
        public String getTime() { return time; }
        public String getIcon() { return icon; }
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public String getSnippet() { return snippet; }
        public UUID getContentId() { return contentId; }
        public String getEventId() { return eventId; }
        public String getFullNotesText() { return fullNotesText; }
        public String getFlashcardData() { return flashcardData; }
        public String getTestData() { return testData; }
        public String getEvaluationData() { return evaluationData; }

        // Setters
        public void setTime(String time) { this.time = time; }
        public void setIcon(String icon) { this.icon = icon; }
        public void setType(String type) { this.type = type; }
        public void setTitle(String title) { this.title = title; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        public void setContentId(UUID contentId) { this.contentId = contentId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public void setFullNotesText(String fullNotesText) { this.fullNotesText = fullNotesText; }
        public void setFlashcardData(String flashcardData) { this.flashcardData = flashcardData; }
        public void setTestData(String testData) { this.testData = testData; }
        public void setEvaluationData(String evaluationData) { this.evaluationData = evaluationData; }
    }
}
