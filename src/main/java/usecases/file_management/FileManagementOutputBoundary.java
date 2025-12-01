package usecases.file_management;

public interface FileManagementOutputBoundary {
    default void prepareFileListView(FileManagementOutputData outputData) {
        // Default implementation - should be overridden by implementing classes
    }

    default void prepareFailView(String errorMessage) {
        // Default implementation - should be overridden by implementing classes
    }
}
