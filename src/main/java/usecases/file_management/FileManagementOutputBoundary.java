package usecases.file_management;

/**
 * Output boundary for the file management use case.
 */
public interface FileManagementOutputBoundary {
  /**
   * Prepares the file list view.
   *
   * @param outputData the output data containing the file list
   */
  default void prepareFileListView(FileManagementOutputData outputData) {
    // Default implementation - should be overridden by implementing classes
  }

  /**
   * Prepares the failure view with an error message.
   *
   * @param errorMessage the error message
   */
  default void prepareFailView(String errorMessage) {
    // Default implementation - should be overridden by implementing classes
  }
}
