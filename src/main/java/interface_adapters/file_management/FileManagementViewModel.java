package interface_adapters.file_management;

import interface_adapters.ViewModel;

/**
 * ViewModel for the file management view.
 */
public class FileManagementViewModel extends ViewModel<FileManagementState> {
  /**
   * Constructs a new FileManagementViewModel.
   */
  public FileManagementViewModel() {
    super("fileManagement");
    setState(new FileManagementState());
  }
}
