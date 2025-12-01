package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course with its ID, name, description, and uploaded files.
 */
public class Course {
  private String courseId;
  private String name;
  private String description;
  private final List<PDFFile> uploadedFiles;

  /**
   * Constructs a new Course with the given parameters.
   *
   * @param courseId the unique identifier for the course
   * @param name the name of the course
   * @param description the description of the course
   */
  public Course(String courseId, String name, String description) {
    this.courseId = courseId;
    this.name = name;
    this.description = description;
    this.uploadedFiles = new ArrayList<>();
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
   * Sets the course name.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the course description.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the course name.
   *
   * @return the course name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the course description.
   *
   * @return the course description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Adds a PDF file to the course.
   *
   * @param file the PDF file to add
   */
  public void addFile(PDFFile file) {
    uploadedFiles.add(file);
  }

  /**
   * Removes a PDF file from the course.
   *
   * @param file the PDF file to remove
   */
  public void removeFile(PDFFile file) {
    uploadedFiles.remove(file);
  }

  /**
   * Gets the list of uploaded PDF files.
   *
   * @return the list of uploaded files
   */
  public List<PDFFile> getUploadedFiles() {
    return uploadedFiles;
  }
}
