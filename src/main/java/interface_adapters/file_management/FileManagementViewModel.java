package interface_adapters.file_management;

import interface_adapters.ViewModel;

public class FileManagementViewModel extends ViewModel<FileManagementState> {
    public FileManagementViewModel() {
        super("fileManagement");
        setState(new FileManagementState());
    }
}
