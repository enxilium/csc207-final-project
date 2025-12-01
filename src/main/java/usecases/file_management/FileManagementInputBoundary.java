package usecases.file_management;

import entities.PDFFile;

public interface FileManagementInputBoundary {
    void uploadFile(String courseId, String filePath);
    void viewFiles(String courseId);
    void deleteFile(String courseId, String filePath);
}
