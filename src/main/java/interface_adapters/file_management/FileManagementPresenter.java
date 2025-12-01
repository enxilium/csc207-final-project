package interface_adapters.file_management;

import interface_adapters.ViewManagerModel;
import usecases.file_management.FileManagementOutputData;
import usecases.file_management.FileManagementOutputBoundary;

public class FileManagementPresenter implements FileManagementOutputBoundary {
    private final FileManagementViewModel fileManagementViewModel;
    private final ViewManagerModel viewManagerModel;

    public FileManagementPresenter(FileManagementViewModel fileManagementViewModel,
                                   ViewManagerModel viewManagerModel) {
        this.fileManagementViewModel = fileManagementViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareFileListView(FileManagementOutputData outputData) {
        FileManagementState state = fileManagementViewModel.getState();
        state.setCourseId(outputData.getCourseId());
        state.setFiles(outputData.getFiles());
        state.setError(null);
        fileManagementViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        FileManagementState state = fileManagementViewModel.getState();
        state.setError(errorMessage);
        fileManagementViewModel.firePropertyChange();
    }
}
