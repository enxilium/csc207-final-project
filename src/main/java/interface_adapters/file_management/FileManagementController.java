package interface_adapters.file_management;

import usecases.file_management.FileManagementInputBoundary;

/**
 * Controller for the file management use case.
 */
public class FileManagementController {
  private final FileManagementInputBoundary fileManagementInteractor;

  /**
   * Constructs a FileManagementController with the given input boundary.
   *
   * @param fileManagementInteractor the interactor for file management operations
   */
  public FileManagementController(FileManagementInputBoundary fileManagementInteractor) {
    this.fileManagementInteractor = fileManagementInteractor;
  }

  /**
   * Uploads a file to a course.
   *
   * @param courseId the course ID
   * @param filePath the path to the file to upload
   */
  public void uploadFile(String courseId, String filePath) {
    fileManagementInteractor.uploadFile(courseId, filePath);
  }

  /**
   * Views files for a course.
   *
   * @param courseId the course ID
   */
  public void viewFiles(String courseId) {
    fileManagementInteractor.viewFiles(courseId);
  }

  /**
   * Deletes a file from a course.
   *
   * @param courseId the course ID
   * @param filePath the path to the file to delete
   */
  public void deleteFile(String courseId, String filePath) {
    fileManagementInteractor.deleteFile(courseId, filePath);
  }
}
