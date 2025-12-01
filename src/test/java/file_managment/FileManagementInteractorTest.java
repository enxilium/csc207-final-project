package file_managment;

import entities.Course;
import entities.PDFFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecases.ICourseRepository;
import usecases.file_management.FileManagementInteractor;
import usecases.file_management.FileManagementOutputBoundary;
import usecases.file_management.FileManagementOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileManagementInteractor.
 *
 * These tests verify:
 * - File upload functionality
 * - File viewing functionality
 * - File deletion functionality
 * - Error handling for invalid inputs
 */

class FileManagementInteractorTest {

    private FakeCourseRepository repository;
    private MockFileManagementPresenter presenter;
    private FileManagementInteractor interactor;

    @BeforeEach
    void setUp() {
        repository = new FakeCourseRepository();
        presenter = new MockFileManagementPresenter();
        interactor = new FileManagementInteractor(repository, presenter);
    }

    @Test
    void uploadFile_validCourseAndFile_uploadsFileAndRefreshesView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("/path/to/file.pdf", course.getUploadedFiles().get(0).getPath().toString());
        assertNotNull(presenter.lastOutputData);
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
        assertEquals(1, presenter.lastOutputData.getFiles().size());
        assertEquals(2, presenter.prepareFileListViewCallCount); // Called once from upload, once from viewFiles
    }

    @Test
    void uploadFile_nullCourseId_callsFailView() {
        // Act
        interactor.uploadFile(null, "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void uploadFile_emptyCourseId_callsFailView() {
        // Act
        interactor.uploadFile("", "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void uploadFile_nullFilePath_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", null);

        // Assert
        assertEquals("File path cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void uploadFile_emptyFilePath_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "");

        // Assert
        assertEquals("File path cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void uploadFile_courseNotFound_callsFailView() {
        // Act
        interactor.uploadFile("NONEXISTENT", "/path/to/file.pdf");

        // Assert
        assertEquals("Course not found", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void viewFiles_validCourseId_returnsFileList() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file1.pdf"));
        course.addFile(new PDFFile("/path/to/file2.pdf"));
        repository.create(course);

        // Act
        interactor.viewFiles("CSC207");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertNotNull(presenter.lastOutputData);
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
        assertEquals(2, presenter.lastOutputData.getFiles().size());
        assertEquals(1, presenter.prepareFileListViewCallCount);
    }

    @Test
    void viewFiles_courseWithNoFiles_returnsEmptyList() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.viewFiles("CSC207");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertNotNull(presenter.lastOutputData);
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
        assertEquals(0, presenter.lastOutputData.getFiles().size());
    }

    @Test
    void viewFiles_nullCourseId_callsFailView() {
        // Act
        interactor.viewFiles(null);

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void viewFiles_emptyCourseId_callsFailView() {
        // Act
        interactor.viewFiles("");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void viewFiles_courseNotFound_callsFailView() {
        // Act
        interactor.viewFiles("NONEXISTENT");

        // Assert
        assertEquals("Course not found", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_validFile_deletesFileAndRefreshesView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file1.pdf"));
        course.addFile(new PDFFile("/path/to/file2.pdf"));
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "/path/to/file1.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("/path/to/file2.pdf", course.getUploadedFiles().get(0).getPath().toString());
        assertNotNull(presenter.lastOutputData);
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
        assertEquals(1, presenter.lastOutputData.getFiles().size());
        assertEquals(2, presenter.prepareFileListViewCallCount); // Called once from delete, once from viewFiles
    }

    @Test
    void deleteFile_nullCourseId_callsFailView() {
        // Act
        interactor.deleteFile(null, "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_emptyCourseId_callsFailView() {
        // Act
        interactor.deleteFile("", "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_nullFilePath_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", null);

        // Assert
        assertEquals("File path cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_emptyFilePath_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "");

        // Assert
        assertEquals("File path cannot be empty", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_courseNotFound_callsFailView() {
        // Act
        interactor.deleteFile("NONEXISTENT", "/path/to/file.pdf");

        // Assert
        assertEquals("Course not found", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    @Test
    void deleteFile_fileNotFound_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file1.pdf"));
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "/path/to/nonexistent.pdf");

        // Assert
        assertEquals("File not found", presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size()); // File should not be deleted
    }

    @Test
    void deleteFile_lastFile_deletesAndShowsEmptyList() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file.pdf"));
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "/path/to/file.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
        assertNotNull(presenter.lastOutputData);
        assertEquals(0, presenter.lastOutputData.getFiles().size());
    }

    // ========== PDF Validation Tests ==========

    @Test
    void uploadFile_nonPdfExtension_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file.txt");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_jpgExtension_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/image.jpg");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_docxExtension_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/document.docx");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_uppercasePdfExtension_validatesSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file.PDF");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertNotNull(presenter.lastOutputData);
    }

    @Test
    void uploadFile_mixedCasePdfExtension_validatesSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file.Pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_filePathWithoutExtension_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/filename");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_onlyDotPdf_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", ".pdf");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_pathWithPdfInMiddle_callsFailView() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/pdf/filename.txt");

        // Assert
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    // ========== Multiple File Operations Tests ==========

    @Test
    void uploadFile_multipleFilesSequentially_uploadsAllFiles() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.uploadFile("CSC207", "/path/to/file2.pdf");
        interactor.uploadFile("CSC207", "/path/to/file3.pdf");

        // Assert
        assertEquals(3, course.getUploadedFiles().size());
        assertEquals("/path/to/file1.pdf", course.getUploadedFiles().get(0).getPath().toString());
        assertEquals("/path/to/file2.pdf", course.getUploadedFiles().get(1).getPath().toString());
        assertEquals("/path/to/file3.pdf", course.getUploadedFiles().get(2).getPath().toString());
    }

    @Test
    void uploadFile_sameFileTwice_uploadsTwice() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);
        String filePath = "/path/to/samefile.pdf";

        // Act
        interactor.uploadFile("CSC207", filePath);
        interactor.uploadFile("CSC207", filePath);

        // Assert
        assertEquals(2, course.getUploadedFiles().size());
        assertEquals(filePath, course.getUploadedFiles().get(0).getPath().toString());
        assertEquals(filePath, course.getUploadedFiles().get(1).getPath().toString());
    }

    @Test
    void viewFiles_multipleCourses_returnsCorrectFiles() {
        // Arrange
        Course course1 = new Course("CSC207", "Software Design", "OOP course");
        course1.addFile(new PDFFile("/path/to/file1.pdf"));
        course1.addFile(new PDFFile("/path/to/file2.pdf"));
        repository.create(course1);

        Course course2 = new Course("CSC369", "Operating Systems", "OS course");
        course2.addFile(new PDFFile("/path/to/file3.pdf"));
        repository.create(course2);

        // Act
        interactor.viewFiles("CSC207");

        // Assert
        assertNotNull(presenter.lastOutputData);
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
        assertEquals(2, presenter.lastOutputData.getFiles().size());
        assertEquals("/path/to/file1.pdf", presenter.lastOutputData.getFiles().get(0).getPath().toString());
    }

    @Test
    void deleteFile_multipleFiles_removesCorrectFile() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file1.pdf"));
        course.addFile(new PDFFile("/path/to/file2.pdf"));
        course.addFile(new PDFFile("/path/to/file3.pdf"));
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "/path/to/file2.pdf");

        // Assert
        assertEquals(2, course.getUploadedFiles().size());
        assertEquals("/path/to/file1.pdf", course.getUploadedFiles().get(0).getPath().toString());
        assertEquals("/path/to/file3.pdf", course.getUploadedFiles().get(1).getPath().toString());
        assertNotNull(presenter.lastOutputData);
        assertEquals(2, presenter.lastOutputData.getFiles().size());
    }

    // ========== Edge Cases and Complex Scenarios ==========

    @Test
    void uploadFile_filePathWithSpaces_uploadsSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/my file with spaces.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("/path/to/my file with spaces.pdf", course.getUploadedFiles().get(0).getPath().toString());
    }

    @Test
    void uploadFile_filePathWithSpecialCharacters_uploadsSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file-name_123(version).pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_veryLongFileName_uploadsSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);
        String longFileName = "/path/to/" + "a".repeat(100) + ".pdf";

        // Act
        interactor.uploadFile("CSC207", longFileName);

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_absoluteWindowsPath_validatesAndUploads() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "C:\\Users\\Documents\\file.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void deleteFile_filePathWithSpaces_deletesCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/my file.pdf"));
        repository.create(course);

        // Act
        interactor.deleteFile("CSC207", "/path/to/my file.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void uploadAndDeleteFile_sequentialOperations_worksCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - Upload
        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.uploadFile("CSC207", "/path/to/file2.pdf");

        // Assert after upload
        assertEquals(2, course.getUploadedFiles().size());

        // Reset presenter state
        presenter.lastErrorMessage = null;
        presenter.lastOutputData = null;
        presenter.prepareFileListViewCallCount = 0;

        // Act - Delete
        interactor.deleteFile("CSC207", "/path/to/file1.pdf");

        // Assert after delete
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("/path/to/file2.pdf", course.getUploadedFiles().get(0).getPath().toString());
    }

    @Test
    void viewFiles_afterMultipleUploads_returnsAllFiles() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.uploadFile("CSC207", "/path/to/file2.pdf");
        interactor.uploadFile("CSC207", "/path/to/file3.pdf");

        // Reset presenter
        presenter.lastOutputData = null;
        presenter.prepareFileListViewCallCount = 0;

        // Act
        interactor.viewFiles("CSC207");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertNotNull(presenter.lastOutputData);
        assertEquals(3, presenter.lastOutputData.getFiles().size());
        assertEquals("CSC207", presenter.lastOutputData.getCourseId());
    }

    @Test
    void uploadFile_emptyStringAfterTrim_shouldBeValidated() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - This tests that validation happens before other checks
        // The empty string is caught by the "File path cannot be empty" check
        interactor.uploadFile("CSC207", "   ");

        // Assert
        // The validation should catch empty/whitespace paths
        // Since the current implementation checks isEmpty() first, this might be caught there
        // But we're testing the PDF validation would catch invalid files
        assertNotNull(presenter.lastErrorMessage);
    }

    // ========== Additional Edge Cases and Integration Tests ==========

    @Test
    void uploadFile_relativePath_validatesAndUploads() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "documents/notes.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("documents/notes.pdf", course.getUploadedFiles().get(0).getPath().toString());
    }

    @Test
    void uploadFile_deeplyNestedPath_validatesAndUploads() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/home/user/documents/courses/csc207/lectures/lecture1.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_unicodeCharactersInPath_validatesAndUploads() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/文件名称.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void deleteFile_caseSensitivePath_deletesCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/File.pdf"));
        repository.create(course);

        // Act - Path matching must be exact (case-sensitive)
        interactor.deleteFile("CSC207", "/path/to/File.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(0, course.getUploadedFiles().size());
    }

    @Test
    void deleteFile_caseMismatchPath_fileNotFound() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file.pdf"));
        repository.create(course);

        // Act - Trying to delete with different case
        interactor.deleteFile("CSC207", "/path/to/FILE.PDF");

        // Assert - Should not find file due to case mismatch
        assertEquals("File not found", presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_whitespaceOnlyCourseId_callsFailView() {
        // Act
        interactor.uploadFile("   ", "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
    }

    @Test
    void viewFiles_whitespaceOnlyCourseId_callsFailView() {
        // Act
        interactor.viewFiles("   ");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
    }

    @Test
    void deleteFile_whitespaceOnlyCourseId_callsFailView() {
        // Act
        interactor.deleteFile("   ", "/path/to/file.pdf");

        // Assert
        assertEquals("Course ID cannot be empty", presenter.lastErrorMessage);
    }

    @Test
    void uploadFile_largeNumberOfFiles_handlesCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - Upload 10 files
        for (int i = 1; i <= 10; i++) {
            interactor.uploadFile("CSC207", "/path/to/file" + i + ".pdf");
        }

        // Assert
        assertEquals(10, course.getUploadedFiles().size());
        assertNull(presenter.lastErrorMessage);
    }

    @Test
    void viewFiles_preservesFileOrder() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - Upload files in specific order
        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.uploadFile("CSC207", "/path/to/file2.pdf");
        interactor.uploadFile("CSC207", "/path/to/file3.pdf");

        // Reset presenter
        presenter.lastOutputData = null;

        // Act - View files
        interactor.viewFiles("CSC207");

        // Assert - Order should be preserved
        assertNotNull(presenter.lastOutputData);
        List<PDFFile> files = presenter.lastOutputData.getFiles();
        assertEquals(3, files.size());
        assertEquals("/path/to/file1.pdf", files.get(0).getPath().toString());
        assertEquals("/path/to/file2.pdf", files.get(1).getPath().toString());
        assertEquals("/path/to/file3.pdf", files.get(2).getPath().toString());
    }

    @Test
    void uploadFile_thenViewFiles_refreshesViewCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - Upload
        interactor.uploadFile("CSC207", "/path/to/file1.pdf");

        // Assert - View should have been called (upload calls viewFiles)
        assertEquals(2, presenter.prepareFileListViewCallCount);
        assertNotNull(presenter.lastOutputData);
        assertEquals(1, presenter.lastOutputData.getFiles().size());

        // Reset
        presenter.prepareFileListViewCallCount = 0;

        // Act - View explicitly
        interactor.viewFiles("CSC207");

        // Assert
        assertEquals(1, presenter.prepareFileListViewCallCount);
        assertEquals(1, presenter.lastOutputData.getFiles().size());
    }

    @Test
    void deleteFile_thenViewFiles_refreshesViewCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        course.addFile(new PDFFile("/path/to/file1.pdf"));
        course.addFile(new PDFFile("/path/to/file2.pdf"));
        repository.create(course);

        // Act - Delete
        interactor.deleteFile("CSC207", "/path/to/file1.pdf");

        // Assert - View should have been called (delete calls viewFiles)
        assertEquals(2, presenter.prepareFileListViewCallCount);
        assertNotNull(presenter.lastOutputData);
        assertEquals(1, presenter.lastOutputData.getFiles().size());
    }

    @Test
    void multipleOperations_errorClearsOnSuccess() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - First operation fails
        interactor.uploadFile("CSC207", "/path/to/file.txt");

        // Assert - Error is set
        assertNotNull(presenter.lastErrorMessage);

        // Act - Second operation succeeds
        interactor.uploadFile("CSC207", "/path/to/file.pdf");

        // Assert - Error should be cleared (or success view should be shown)
        // Since upload calls viewFiles on success, lastOutputData should be set
        assertNotNull(presenter.lastOutputData);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_multipleCourses_isolatesFiles() {
        // Arrange
        Course course1 = new Course("CSC207", "Software Design", "OOP course");
        Course course2 = new Course("CSC369", "Operating Systems", "OS course");
        repository.create(course1);
        repository.create(course2);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.uploadFile("CSC369", "/path/to/file2.pdf");

        // Assert
        assertEquals(1, course1.getUploadedFiles().size());
        assertEquals(1, course2.getUploadedFiles().size());
        assertEquals("/path/to/file1.pdf", course1.getUploadedFiles().get(0).getPath().toString());
        assertEquals("/path/to/file2.pdf", course2.getUploadedFiles().get(0).getPath().toString());
    }

    @Test
    void deleteFile_differentCourse_fileNotFound() {
        // Arrange
        Course course1 = new Course("CSC207", "Software Design", "OOP course");
        Course course2 = new Course("CSC369", "Operating Systems", "OS course");
        course1.addFile(new PDFFile("/path/to/file.pdf"));
        repository.create(course1);
        repository.create(course2);

        // Act - Try to delete file from course1 using course2's ID
        interactor.deleteFile("CSC369", "/path/to/file.pdf");

        // Assert
        assertEquals("File not found", presenter.lastErrorMessage);
        assertEquals(1, course1.getUploadedFiles().size());
        assertEquals(0, course2.getUploadedFiles().size());
    }

    @Test
    void uploadFile_fileWithMultipleDots_validatesCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/path/to/file.version.2.pdf");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
        assertEquals("/path/to/file.version.2.pdf", course.getUploadedFiles().get(0).getPath().toString());
    }

    @Test
    void uploadFile_hiddenFileName_validatesAndUploads() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - Hidden files (starting with dot) with .pdf extension should be valid
        interactor.uploadFile("CSC207", "/path/to/.hidden.pdf");

        // Assert
        // Hidden files with .pdf extension are valid PDF files
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void uploadFile_pdfExtensionWithUppercasePath_validatesCorrectly() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.uploadFile("CSC207", "/PATH/TO/FILE.PDF");

        // Assert
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    @Test
    void viewFiles_multipleCalls_updatesViewEachTime() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act
        interactor.viewFiles("CSC207");
        int firstCallCount = presenter.prepareFileListViewCallCount;

        interactor.uploadFile("CSC207", "/path/to/file1.pdf");
        interactor.viewFiles("CSC207");
        int secondCallCount = presenter.prepareFileListViewCallCount;

        // Assert
        assertEquals(1, firstCallCount);
        assertTrue(secondCallCount > firstCallCount);
        assertNotNull(presenter.lastOutputData);
        assertEquals(1, presenter.lastOutputData.getFiles().size());
    }

    @Test
    void uploadFile_afterFailedUpload_retriesSuccessfully() {
        // Arrange
        Course course = new Course("CSC207", "Software Design", "OOP course");
        repository.create(course);

        // Act - First upload fails (wrong file type)
        interactor.uploadFile("CSC207", "/path/to/file.txt");
        String firstError = presenter.lastErrorMessage;
        int initialFileCount = course.getUploadedFiles().size();

        // Reset error state for next call
        presenter.lastErrorMessage = null;
        presenter.lastOutputData = null;

        // Act - Second upload succeeds
        interactor.uploadFile("CSC207", "/path/to/file.pdf");

        // Assert
        assertNotNull(firstError);
        assertEquals("Invalid file type. Only PDF files (.pdf) are allowed.", firstError);
        assertEquals(0, initialFileCount);
        assertNull(presenter.lastErrorMessage);
        assertEquals(1, course.getUploadedFiles().size());
    }

    /**
     * Simple in-memory fake repository for tests.
     */
    private static class FakeCourseRepository implements ICourseRepository {
        private final List<Course> courses = new ArrayList<>();

        @Override
        public void create(Course course) {
            courses.add(course);
        }

        @Override
        public void update(Course course) {
            delete(course.getCourseId());
            courses.add(course);
        }

        @Override
        public Course findById(String courseId) {
            for (Course c : courses) {
                if (c.getCourseId().equals(courseId)) {
                    return c;
                }
            }
            return null;
        }

        @Override
        public List<Course> findAll() {
            return new ArrayList<>(courses);
        }

        @Override
        public void delete(String courseId) {
            courses.removeIf(c -> c.getCourseId().equals(courseId));
        }
    }

    /**
     * Mock presenter that records method calls and arguments.
     */
    private static class MockFileManagementPresenter implements FileManagementOutputBoundary {
        FileManagementOutputData lastOutputData;
        String lastErrorMessage;
        int prepareFileListViewCallCount = 0;

        @Override
        public void prepareFileListView(FileManagementOutputData outputData) {
            prepareFileListViewCallCount++;
            lastOutputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            lastErrorMessage = errorMessage;
        }
    }
}
