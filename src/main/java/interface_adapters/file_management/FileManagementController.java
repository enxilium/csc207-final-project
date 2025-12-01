package interface_adapters.file_management;

import usecases.file_management.FileManagementInputBoundary;

public class FileManagementController {
    private final FileManagementInputBoundary fileManagementInteractor;

    public FileManagementController(FileManagementInputBoundary fileManagementInteractor) {
        this.fileManagementInteractor = fileManagementInteractor;
    }

    public void uploadFile(String courseId, String filePath) {
        fileManagementInteractor.uploadFile(courseId, filePath);
    }

    public void viewFiles(String courseId) {
        fileManagementInteractor.viewFiles(courseId);
    }

    public void deleteFile(String courseId, String filePath) {
        fileManagementInteractor.deleteFile(courseId, filePath);
    }
}
