package entities;

import java.util.Date;

/**
 * Entity representing generated content with an ID and creation date.
 */
public class GeneratedContent {
  private final String contentId;
  private final Date creationDate;

  /**
   * Constructs a GeneratedContent with the given content ID and creation date.
   *
   * @param contentId the content ID (cannot be empty)
   * @param creationDate the creation date (cannot be null)
   * @throws IllegalArgumentException if contentId is empty or creationDate is null
   */
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
