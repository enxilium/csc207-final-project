package entities;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseID;
    private String name;
    private String description;
    private final List<PDFFile> uploadedFiles;

    public Course(String courseID, String name, String description) {
        this.courseID = courseID;
        this.name = name;
        this.description = description;
        this.uploadedFiles = new ArrayList<>();
    }

    public String getCourseId() {
        return courseID;
    }

    public void setCourseId(String courseID) {
        this.courseID = courseID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public void addFile(PDFFile file) {
        uploadedFiles.add(file);
    }

    public void removeFile(PDFFile file) {
        uploadedFiles.remove(file);
    }

    public List<PDFFile> getUploadedFiles() {
        return uploadedFiles;
    }
}
