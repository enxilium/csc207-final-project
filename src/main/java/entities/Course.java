package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course in the application.
 */
public class Course {
    private String courseID;
    private String name;
    private String description;
    private final List<PDFFile> uploadedFiles;

    /**
     * Constructs a new Course.
     * @param courseID The unique identifier for the course.
     * @param name The name of the course.
     * @param description The description of the course.
     */
    public Course(String courseID, String name, String description) {
        this.courseID = courseID;
        this.name = name;
        this.description = description;
        this.uploadedFiles = new ArrayList<>();
    }

    /**
     * Gets the course ID.
     * @return The course ID.
     */
    public String getCourseId() {
        return courseID;
    }

    /**
     * Sets the course ID.
     * @param id The new course ID.
     */
    public void setCourseId(String id) {
        this.courseID = id;
    }

    /**
     * Sets the course name.
     * @param name The new course name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the course description.
     * @param description The new course description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the course name.
     * @return The course name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the course description.
     * @return The course description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds a file to the course.
     * @param file The file to add.
     */
    public void addFile(PDFFile file) {
        uploadedFiles.add(file);
    }

    /**
     * Removes a file from the course.
     * @param file The file to remove.
     */
    public void removeFile(PDFFile file) {
        uploadedFiles.remove(file);
    }

    /**
     * Gets the list of uploaded files.
     * @return The list of uploaded files.
     */
    public List<PDFFile> getUploadedFiles() {
        return uploadedFiles;
    }
}
