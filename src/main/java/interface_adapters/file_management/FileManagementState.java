package interface_adapters.file_management;

import entities.PDFFile;
import java.util.List;

public class FileManagementState {
    private String courseId;
    private List<PDFFile> files;
    private String error;

    public FileManagementState() {
        //
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<PDFFile> getFiles() {
        return files;
    }

    public void setFiles(List<PDFFile> files) {
        this.files = files;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
