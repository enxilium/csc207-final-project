package entities;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseID;
    private List<PDFFile> uploadedFiles;
    private String name;
    private String description;

    private List<GeneratedContent> generatedContent;
    /**
     * Creates a new user with the given non-empty name and non-empty password.
     * @throws IllegalArgumentException if the password or name are empty
     */
    public Course(String courseId, String name, String description) {
        if ("".equals(courseId)) {
            throw new IllegalArgumentException("courseId cannot be empty");
        }

        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        this.courseID = courseId;
        this.name = name;
        this.description = description;
        this.uploadedFiles = new ArrayList<PDFFile>();
        this.generatedContent = new ArrayList<GeneratedContent>();
    }

    public String getCourseId() {
        return courseID;
    }
    public void setCourseId(String courseId) {
        this.courseID = courseId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void addFile(PDFFile file) {
        uploadedFiles.add(file);
    }

    public List<PDFFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String discription) {
        this.description = discription;
    }

    @Override
    public boolean equals(Object obj) {
        Course other = (Course) obj;
        return this.courseID.equals(other.courseID);
    }
}
