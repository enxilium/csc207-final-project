package usecases.file_management;

public interface FileManagementInputBoundary {
    void uploadFile(String courseId, String filePath);
    void viewFiles(String courseId);
    void deleteFile(String courseId, String filePath);
}
