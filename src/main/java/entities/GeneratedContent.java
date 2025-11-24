package entities;
import java.util.Date;

public class GeneratedContent {
    private final String contentId;
    private final Date creationDate;

    public GeneratedContent(String contentId, Date creationDate) {
        if ("".equals(contentId)) {
            throw new IllegalArgumentException("contentId cannot be empty");
        }

        if (creationDate == null) {
            throw new IllegalArgumentException("creationDate cannot be empty");
        }
        this.contentId = contentId;
        this.creationDate = creationDate;
    }
}
