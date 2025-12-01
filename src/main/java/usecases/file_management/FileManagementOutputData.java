package usecases.file_management;

import entities.PDFFile;
import java.util.List;

/**
 * Output data model for the file management use case.
 */
public class FileManagementOutputData {
  private final List<PDFFile> files;
  private final String courseId;

  /**
   * Constructs a FileManagementOutputData with the given course ID and files.
   *
   * @param courseId the course ID
   * @param files the list of PDF files
   */
  public FileManagementOutputData(String courseId, List<PDFFile> files) {
    this.courseId = courseId;
    this.files = files;
  }

  /**
   * Gets the list of PDF files.
   *
   * @return the list of PDF files
   */
  public List<PDFFile> getFiles() {
    return files;
  }

  /**
   * Gets the course ID.
   *
   * @return the course ID
   */
  public String getCourseId() {
    return courseId;
  }
}
