package interface_adapters.file_management;

import entities.PDFFile;
import java.util.List;

/**
 * State class for the file management view model.
 */
public class FileManagementState {
  private String courseId;
  private List<PDFFile> files;
  private String error;

  /**
   * Constructs a new FileManagementState.
   */
  public FileManagementState() {
    //
  }

  /**
   * Gets the course ID.
   *
   * @return the course ID
   */
  public String getCourseId() {
    return courseId;
  }

  /**
   * Sets the course ID.
   *
   * @param courseId the course ID to set
   */
  public void setCourseId(String courseId) {
    this.courseId = courseId;
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
   * Sets the list of PDF files.
   *
   * @param files the list of PDF files to set
   */
  public void setFiles(List<PDFFile> files) {
    this.files = files;
  }

  /**
   * Gets the error message.
   *
   * @return the error message
   */
  public String getError() {
    return error;
  }

  /**
   * Sets the error message.
   *
   * @param error the error message to set
   */
  public void setError(String error) {
    this.error = error;
  }
}
