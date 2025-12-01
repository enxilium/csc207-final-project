package usecases.file_management;

import entities.PDFFile;
import java.util.List;

public class FileManagementOutputData {
    private final List<PDFFile> files;
    private final String courseId;

    public FileManagementOutputData(String courseId, List<PDFFile> files) {
        this.courseId = courseId;
        this.files = files;
    }

    public List<PDFFile> getFiles() {
        return files;
    }

    public String getCourseId() {
        return courseId;
    }
}
