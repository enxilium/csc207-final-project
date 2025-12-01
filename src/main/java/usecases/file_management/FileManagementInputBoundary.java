package usecases.file_management;

/**
 * Input boundary for the file management use case.
 */
public interface FileManagementInputBoundary {
  /**
   * Uploads a file to a course.
   *
   * @param courseId the course ID
   * @param filePath the path to the file to upload
   */
  void uploadFile(String courseId, String filePath);

  /**
   * Views files for a course.
   *
   * @param courseId the course ID
   */
  void viewFiles(String courseId);

  /**
   * Deletes a file from a course.
   *
   * @param courseId the course ID
   * @param filePath the path to the file to delete
   */
  void deleteFile(String courseId, String filePath);
}
