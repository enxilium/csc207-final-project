package views;

import entities.Course;
import entities.PDFFile;
import interface_adapters.ViewManagerModel;
import interface_adapters.dashboard.CourseDashboardController;
import interface_adapters.file_management.FileManagementController;
import interface_adapters.flashcards.GenerateFlashcardsController;
import interface_adapters.mock_test.MockTestController;
import interface_adapters.mock_test.MockTestViewModel;
import interface_adapters.workspace.CourseController;
import interface_adapters.workspace.CourseState;
import interface_adapters.workspace.CourseWorkspaceViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * View for displaying the course workspace.
 */
public class CourseWorkspaceView extends JPanel implements ActionListener, PropertyChangeListener {
  private final String viewName = "workspace";
  private JPanel centerPanel = null; // To keep track of the previously selected button
  private CourseDashboardController courseDashboardController = null;
  private MockTestController mockTestController = null;
  private CourseController courseController = null;
  private FileManagementController fileManagementController = null;
  private ViewManagerModel viewManagerModel = null;
  private MockTestViewModel mockTestViewModel = null;
  private String courseId = null;
  private String lastUploadedPdfPath = null;
  private CourseWorkspaceViewModel courseWorkspaceViewModel = null;
  private Runnable openLectureNotesAction = null;
  private Runnable openTimelineAction = null;
  private GenerateFlashcardsController flashcardsController;

  /**
   * Constructs a CourseWorkspaceView with the given view model.
   *
   * @param courseWorkspaceViewModel the view model for the workspace
   */
  public CourseWorkspaceView(CourseWorkspaceViewModel courseWorkspaceViewModel) {
    this.courseWorkspaceViewModel = courseWorkspaceViewModel;
    this.courseWorkspaceViewModel.addPropertyChangeListener(this);

    this.setPreferredSize(new Dimension(1200, 800));

    this.setBackground(Color.LIGHT_GRAY);
    this.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Main panel using BorderLayout
    this.setLayout(new BorderLayout());

    // Top panel
    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.LIGHT_GRAY);
    topPanel.add(new JLabel("Course Workspace"));
    this.add(topPanel, BorderLayout.NORTH);

    // Center panel (optional, can be used for main content)
    centerPanel = new JPanel();
    centerPanel.setBackground(Color.WHITE);
    this.add(centerPanel, BorderLayout.CENTER);

    // command panel
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(Color.DARK_GRAY);

    // edit
    JButton editButton = new JButton("Edit");
    editButton.setPreferredSize(new Dimension(80, 30));
    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton currentButton = (JButton) e.getSource();
        courseController.editCourse(courseId);
      }
    });
    bottomPanel.add(editButton);

    // delete
    JButton deleteButton = new JButton("Delete");
    deleteButton.setPreferredSize(new Dimension(80, 30));
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton currentButton = (JButton) e.getSource();
        courseController.deleteCourse(courseId);
      }
    });
    bottomPanel.add(deleteButton);

    // upload content
    JButton uploadButton = new JButton("Upload");
    uploadButton.setPreferredSize(new Dimension(120, 30));
    uploadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileManagementController != null && courseId != null) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Select a PDF file to upload");
          fileChooser.setFileFilter(
              new FileNameExtensionFilter("PDF Files", "pdf"));
          int result = fileChooser.showOpenDialog(CourseWorkspaceView.this);
          if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            lastUploadedPdfPath = filePath;
            fileManagementController.uploadFile(courseId, filePath);
            // Refresh the view to show the new file
            fileManagementController.viewFiles(courseId);
          }
        }
      }
    });
    bottomPanel.add(uploadButton);

    // open uploaded pdf files
    JButton noteButton = new JButton("Existing Files");
    noteButton.setPreferredSize(new Dimension(140, 30));
    noteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileManagementController != null && courseId != null) {
          fileManagementController.viewFiles(courseId);
          if (viewManagerModel != null) {
            viewManagerModel.setState("fileManagement");
            viewManagerModel.firePropertyChange();
          }
        }
      }
    });
    bottomPanel.add(noteButton);

    // create note
    JButton createNoteButton = new JButton("Create Notes");
    createNoteButton.setPreferredSize(new Dimension(140, 30));
    bottomPanel.add(createNoteButton);
    createNoteButton.addActionListener(e -> {
      if (openLectureNotesAction != null) {
        openLectureNotesAction.run();
      }
    });

    // create flash card
    JButton createFlashCardButton = new JButton("Create Flashcards");
    createFlashCardButton.setPreferredSize(new Dimension(150, 30));
    bottomPanel.add(createFlashCardButton);
    createFlashCardButton.addActionListener(e -> {
      if (flashcardsController == null || courseId == null) {
        return;
      }
      // Get the course from the view model state to access uploaded files
      CourseState state = courseWorkspaceViewModel.getState();
      Course course = state != null ? state.getCourse() : null;
      if (course == null) {
        JOptionPane.showMessageDialog(this, "Course not loaded.");
        return;
      }
      // Get uploaded PDF files for the course
      List<PDFFile> uploadedFiles = course.getUploadedFiles();
      if (uploadedFiles == null || uploadedFiles.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please upload a PDF file first using the Upload button.");
        return;
      }
      // Use the first uploaded PDF file for flashcard generation
      String pdfPath = uploadedFiles.get(0).getPath().toString();
      flashcardsController.generateFlashcards(courseId, pdfPath);
    });

    // create test (generate + open test screen)
    JButton createtestButton = new JButton("Create Tests");
    createtestButton.setPreferredSize(new Dimension(150, 30));
    bottomPanel.add(createtestButton);

    createtestButton.addActionListener(e -> {
      if (mockTestController == null || courseId == null) {
        JOptionPane.showMessageDialog(CourseWorkspaceView.this,
            "MockTestController is not set or course is not selected.");
        return;
      }

      // Trigger generation (presenter will navigate to "mock test" after success)
      mockTestController.execute(courseId);

      // Optional: immediate navigation (safe even if generation is async)
      if (viewManagerModel != null) {
        viewManagerModel.setState("mock test");
        viewManagerModel.firePropertyChange();
      }
    });

    // open history/timeline
    JButton historyButton = new JButton("History");
    historyButton.setPreferredSize(new Dimension(120, 30));
    bottomPanel.add(historyButton);
    historyButton.addActionListener(e -> {
      if (openTimelineAction != null) {
        openTimelineAction.run();
      }
    });

    // return
    JButton returnButton = new JButton("Return");
    returnButton.setPreferredSize(new Dimension(80, 30));
    returnButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        courseDashboardController.displayCourses();
      }
    });
    bottomPanel.add(returnButton);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("state")) {
      this.centerPanel.removeAll();
      final CourseState state = (CourseState) evt.getNewValue();
      Course course = state.getCourse();
      this.courseId = course.getCourseId();
      JLabel textLabel = new JLabel("<html>ID: " + course.getCourseId()
          + "<br/> "
          + "Name: " + course.getName()
          + "<br/> "
          + "Description: " + course.getDescription() + "</html>");
      centerPanel.add(textLabel);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Code to execute when the action event occurs
    System.out.println("Action performed!");
  }

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return viewName;
  }

  /**
   * Sets the course dashboard controller.
   *
   * @param courseDashboardController the controller to set
   */
  public void setCourseDashboardController(CourseDashboardController courseDashboardController) {
    this.courseDashboardController = courseDashboardController;
  }

  /**
   * Sets the course workspace controller.
   *
   * @param courseController the controller to set
   */
  public void setCourseWorkspaceController(CourseController courseController) {
    this.courseController = courseController;
  }

  /**
   * Gets the mock test controller.
   *
   * @return the mock test controller
   */
  public MockTestController getMockTestController() {
    return mockTestController;
  }

  /**
   * Sets the mock test controller.
   *
   * @param mockTestController the controller to set
   */
  public void setMockTestController(MockTestController mockTestController) {
    this.mockTestController = mockTestController;
  }

  /**
   * Sets the file management controller.
   *
   * @param fileManagementController the controller to set
   */
  public void setFileManagementController(FileManagementController fileManagementController) {
    this.fileManagementController = fileManagementController;
  }

  /**
   * Sets the view manager model.
   *
   * @param viewManagerModel the model to set
   */
  public void setViewManagerModel(ViewManagerModel viewManagerModel) {
    this.viewManagerModel = viewManagerModel;
  }

  /**
   * Sets the action to open lecture notes.
   *
   * @param action the action to set
   */
  public void setOpenLectureNotesAction(Runnable action) {
    this.openLectureNotesAction = action;
  }

  /**
   * Sets the flashcards controller.
   *
   * @param flashcardsController the controller to set
   */
  public void setFlashcardsController(GenerateFlashcardsController flashcardsController) {
    this.flashcardsController = flashcardsController;
  }

  /**
   * Sets the action to open timeline.
   *
   * @param action the action to set
   */
  public void setOpenTimelineAction(Runnable action) {
    this.openTimelineAction = action;
  }

  /**
   * Sets the mock test view model.
   *
   * @param mockTestViewModel the view model to set
   */
  public void setMockTestViewModel(MockTestViewModel mockTestViewModel) {
    this.mockTestViewModel = mockTestViewModel;
  }
}
