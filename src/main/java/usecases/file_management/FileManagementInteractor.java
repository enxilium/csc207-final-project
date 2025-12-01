package usecases.file_management;

import entities.Course;
import entities.PDFFile;
import java.util.List;
import usecases.ICourseRepository;

/**
 * Interactor for the file management use case.
 */
public class FileManagementInteractor implements FileManagementInputBoundary {
  private final ICourseRepository courseRepository;
  private final FileManagementOutputBoundary fileManagementPresenter;

  /**
   * Constructs a FileManagementInteractor with the given repository and presenter.
   *
   * @param courseRepository the repository for accessing course data
   * @param fileManagementPresenter the presenter for preparing file management views
   */
  public FileManagementInteractor(ICourseRepository courseRepository,
      FileManagementOutputBoundary fileManagementPresenter) {
    this.courseRepository = courseRepository;
    this.fileManagementPresenter = fileManagementPresenter;
  }

  @Override
  public void uploadFile(String courseId, String filePath) {
    if (courseId == null || courseId.isEmpty()) {
      fileManagementPresenter.prepareFailView("Course ID cannot be empty");
      return;
    }
    if (filePath == null || filePath.isEmpty()) {
      fileManagementPresenter.prepareFailView("File path cannot be empty");
      return;
    }

    Course course = courseRepository.findById(courseId);
    if (course == null) {
      fileManagementPresenter.prepareFailView("Course not found");
      return;
    }

    PDFFile pdfFile = new PDFFile(filePath);
    course.addFile(pdfFile);
    courseRepository.update(course);

    // Refresh the file list view
    viewFiles(courseId);
  }

  @Override
  public void viewFiles(String courseId) {
    if (courseId == null || courseId.isEmpty()) {
      fileManagementPresenter.prepareFailView("Course ID cannot be empty");
      return;
    }

    Course course = courseRepository.findById(courseId);
    if (course == null) {
      fileManagementPresenter.prepareFailView("Course not found");
      return;
    }

    List<PDFFile> files = course.getUploadedFiles();
    FileManagementOutputData outputData = new FileManagementOutputData(courseId, files);
    fileManagementPresenter.prepareFileListView(outputData);
  }

  @Override
  public void deleteFile(String courseId, String filePath) {
    if (courseId == null || courseId.isEmpty()) {
      fileManagementPresenter.prepareFailView("Course ID cannot be empty");
      return;
    }
    if (filePath == null || filePath.isEmpty()) {
      fileManagementPresenter.prepareFailView("File path cannot be empty");
      return;
    }

    Course course = courseRepository.findById(courseId);
    if (course == null) {
      fileManagementPresenter.prepareFailView("Course not found");
      return;
    }

    List<PDFFile> files = course.getUploadedFiles();
    boolean removed = files.removeIf(file -> file.getPath().toString().equals(filePath));

    if (!removed) {
      fileManagementPresenter.prepareFailView("File not found");
      return;
    }

    courseRepository.update(course);

    // Refresh the file list view
    viewFiles(courseId);
  }
}
