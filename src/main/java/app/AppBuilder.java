package app;

import data_access.FileTimelineRepository;
import data_access.FlashcardGenerator;
import data_access.GeminiApiDataAccess;
import data_access.ITimelineRepository;
import data_access.LocalCourseRepository;
import entities.Course;
import entities.PDFFile;
import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.dashboard.CourseDashboardController;
import interface_adapters.dashboard.CourseDashboardPresenter;
import interface_adapters.dashboard.CourseDashboardViewModel;
import interface_adapters.evaluate_test.EvaluateTestController;
import interface_adapters.evaluate_test.EvaluateTestPresenter;
import interface_adapters.evaluate_test.EvaluateTestViewModel;
import interface_adapters.file_management.FileManagementController;
import interface_adapters.file_management.FileManagementPresenter;
import interface_adapters.file_management.FileManagementViewModel;
import interface_adapters.flashcards.FlashcardViewModel;
import interface_adapters.flashcards.GenerateFlashcardsController;
import interface_adapters.flashcards.GenerateFlashcardsPresenter;
import interface_adapters.mock_test.MockTestController;
import interface_adapters.mock_test.MockTestPresenter;
import interface_adapters.mock_test.MockTestViewModel;
import interface_adapters.timeline.TimelineController;
import interface_adapters.timeline.ViewTimelineSwingPresenter;
import interface_adapters.timeline.ViewTimelineViewModel;
import interface_adapters.workspace.CourseController;
import interface_adapters.workspace.CourseCreateViewModel;
import interface_adapters.workspace.CourseEditViewModel;
import interface_adapters.workspace.CoursePresenter;
import interface_adapters.workspace.CourseWorkspaceViewModel;
import java.awt.CardLayout;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import usecases.GenerateFlashcardsInputBoundary;
import usecases.GenerateFlashcardsInteractor;
import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsResponseModel;
import usecases.ICourseRepository;
import usecases.Timeline.CourseIdMapper;
import usecases.Timeline.TimelineLogger;
import usecases.Timeline.ViewTimelineInputBoundary;
import usecases.Timeline.ViewTimelineInteractor;
import usecases.Timeline.ViewTimelineOutputBoundary;
import usecases.Timeline.ViewTimelineResponse;
import usecases.dashboard.CourseDashboardInputBoundary;
import usecases.dashboard.CourseDashboardInteractor;
import usecases.dashboard.CourseDashboardOutputBoundary;
import usecases.evaluate_test.EvaluateTestInteractor;
import usecases.evaluate_test.EvaluateTestOutputBoundary;
import usecases.evaluate_test.EvaluateTestOutputData;
import usecases.file_management.FileManagementInputBoundary;
import usecases.file_management.FileManagementInteractor;
import usecases.file_management.FileManagementOutputBoundary;
import usecases.mock_test_generation.MockTestGenerationInteractor;
import usecases.mock_test_generation.MockTestGenerationOutputBoundary;
import usecases.mock_test_generation.MockTestGenerationOutputData;
import usecases.workspace.CourseWorkspaceInputBoundary;
import usecases.workspace.CourseWorkspaceInteractor;
import usecases.workspace.CourseWorkspaceOutputBoundary;
import views.CourseCreateView;
import views.CourseDashboardView;
import views.CourseEditView;
import views.CourseWorkspaceView;
import views.EvaluateTestView;
import views.FileManagementView;
import views.FlashcardDisplayView;
import views.GenerateFlashcardsView;
import views.LectureNotesView;
import views.LoadingView;
import views.ViewTimelineView;
import views.WriteTestView;

/**
 * Builder class for constructing the application with all its components.
 */
public class AppBuilder {
  // --- Shared Components held by the Builder ---
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel cardPanel = new JPanel(cardLayout);
  private final ViewManagerModel viewManagerModel = new ViewManagerModel();

  // --- Data Access Objects ---
  private LocalCourseRepository courseDao = new LocalCourseRepository();
  private GeminiApiDataAccess geminiDao;

  /**
   * Lazy initialization of GeminiApiDataAccess to ensure API key is set first.
   *
   * @return the GeminiApiDataAccess instance
   */
  private GeminiApiDataAccess getGeminiDao() {
    if (geminiDao == null) {
      geminiDao = new GeminiApiDataAccess();
    }
    return geminiDao;
  }

  // --- ViewModels and Views (stored for wiring) ---
  private MockTestViewModel mockTestViewModel;
  private EvaluateTestViewModel evaluateTestViewModel;
  private LoadingViewModel loadingViewModel;
  private WriteTestView writeTestView;
  private EvaluateTestView evaluateTestView;
  // Lecture notes
  private interface_adapters.lecturenotes.LectureNotesViewModel lectureNotesViewModel;
  private views.LectureNotesView lectureNotesView;

  // === SHIRLEY: Course dashboard/workspace view models & views ===
  private CourseDashboardViewModel courseDashboardViewModel;
  private CourseDashboardView courseDashboardView;

  private CourseWorkspaceViewModel courseWorkspaceViewModel;
  private CourseWorkspaceView courseWorkspaceView;

  private CourseCreateViewModel courseCreateViewModel;
  private CourseCreateView courseCreateView;

  private CourseEditViewModel courseEditViewModel;
  private CourseEditView courseEditView;

  // === Soumil: File management view models and views ===
  private FileManagementViewModel fileManagementViewModel;
  private FileManagementView fileManagementView;

  // === WENLE: Flashcard view models & views ===
  private FlashcardViewModel flashcardViewModel;
  private GenerateFlashcardsView generateFlashcardsView;
  private FlashcardDisplayView flashcardDisplayView;

  // === IAIN: Timeline view models & views ===
  private interface_adapters.timeline.ViewTimelineViewModel timelineViewModel;
  private views.ViewTimelineView timelineView;
  private interface_adapters.timeline.TimelineController timelineController;
  private final ITimelineRepository timelineRepository =
      new data_access.FileTimelineRepository();
  private final TimelineLogger timelineLogger = new TimelineLogger(timelineRepository);

  /**
   * Constructs a new AppBuilder instance.
   */
  public AppBuilder() {
    // Optionally leave empty, or only do non-demo initialization
  }

  /**
   * Adds the write test view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addWriteTestView() {
    mockTestViewModel = new MockTestViewModel();
    writeTestView = new WriteTestView(mockTestViewModel);
    cardPanel.add(writeTestView, mockTestViewModel.getViewName());
    return this;
  }

  /**
   * Adds the evaluate test view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addEvaluateTestView() {
    evaluateTestViewModel = new EvaluateTestViewModel();
    evaluateTestView = new EvaluateTestView(evaluateTestViewModel);
    cardPanel.add(evaluateTestView, evaluateTestViewModel.getViewName());
    return this;
  }

  /**
   * Adds the loading view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addLoadingView() {
    loadingViewModel = new LoadingViewModel();
    LoadingView loadingView = new LoadingView(loadingViewModel);
    cardPanel.add(loadingView, loadingViewModel.getViewName());
    return this;
  }

  /**
   * Adds the mock test generation use case to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addMockTestGenerationUseCase() {
    // Create original presenter
    MockTestPresenter originalPresenter =
        new MockTestPresenter(mockTestViewModel, viewManagerModel, loadingViewModel);

    // Wrap presenter to add Timeline logging
    MockTestGenerationOutputBoundary presenter =
        new MockTestGenerationOutputBoundary() {
          @Override
          public void presentTest(MockTestGenerationOutputData outputData) {
            originalPresenter.presentTest(outputData);

            // Log to Timeline
            try {
              String courseId = outputData.getCourseId();
              if (courseId != null && !courseId.isEmpty()) {
                UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseId);
                UUID contentId = UUID.randomUUID();
                int numQuestions = outputData.getQuestions() != null
                    ? outputData.getQuestions().size()
                    : 0;
                // Serialize to JSON in the framework layer (not in use case)
                com.google.gson.Gson gson = new com.google.gson.Gson();
                String testDataJson = gson.toJson(outputData);
                timelineLogger.logQuizGenerated(
                    courseUuid, contentId, numQuestions, testDataJson);
              }
            } catch (Exception e) {
              // Don't break the flow if Timeline logging fails
            }
          }

          @Override
          public void presentLoading() {
            originalPresenter.presentLoading();
          }

          @Override
          public void presentError(String error) {
            originalPresenter.presentError(error);
          }
        };

    MockTestGenerationInteractor interactor =
        new MockTestGenerationInteractor(courseDao, getGeminiDao(), presenter);
    MockTestController controller = new MockTestController(interactor);
    this.courseWorkspaceView.setMockTestController(controller);

    return this;
  }

  /**
   * Adds the evaluate test use case to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addEvaluateTestUseCase() {
    // The original Presenter for the evaluation results view
    EvaluateTestPresenter originalEvalPresenter =
        new EvaluateTestPresenter(
            evaluateTestViewModel, loadingViewModel, courseDashboardViewModel, viewManagerModel);

    // Wrap presenter to add Timeline logging
    EvaluateTestOutputBoundary evalPresenter =
        new EvaluateTestOutputBoundary() {
          @Override
          public void presentEvaluationResults(EvaluateTestOutputData evaluateTestOutputData) {
            originalEvalPresenter.presentEvaluationResults(evaluateTestOutputData);

            // Log to Timeline
            try {
              // Get courseId from MockTestViewModel state
              if (mockTestViewModel != null) {
                var mockTestState = mockTestViewModel.getState();
                String courseId = mockTestState != null ? mockTestState.getCourseId() : null;

                if (courseId != null && !courseId.isEmpty()) {
                  UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseId);
                  UUID contentId = UUID.randomUUID();
                  int numQuestions = evaluateTestOutputData.getQuestions() != null
                      ? evaluateTestOutputData.getQuestions().size()
                      : 0;
                  double score = evaluateTestOutputData.getScore();
                  // Serialize to JSON in the framework layer (not in use case)
                  com.google.gson.Gson gson = new com.google.gson.Gson();
                  String evaluationDataJson = gson.toJson(evaluateTestOutputData);
                  timelineLogger.logQuizSubmitted(
                      courseUuid, contentId, numQuestions, score, evaluationDataJson);
                }
              }
            } catch (Exception e) {
              // Don't break the flow if Timeline logging fails
            }
          }

          @Override
          public void presentLoading() {
            originalEvalPresenter.presentLoading();
          }

          @Override
          public void presentError(String errorMessage) {
            originalEvalPresenter.presentError(errorMessage);
          }
        };

    // The Interactor for the evaluation use case. It correctly uses the DAOs.
    EvaluateTestInteractor evalInteractor =
        new EvaluateTestInteractor(courseDao, getGeminiDao(), evalPresenter);

    // The Controller that the WriteTestView will use to trigger the evaluation.
    EvaluateTestController evalController = new EvaluateTestController(evalInteractor);

    // The Presenter for the WriteTestView's navigation (next/prev question).
    MockTestPresenter mockTestPresenter =
        new MockTestPresenter(mockTestViewModel, viewManagerModel, loadingViewModel);

    // Inject both the controller (for submitting) and the presenter
    // (for navigation) into the WriteTestView.
    writeTestView.setController(evalController);
    writeTestView.setPresenter(mockTestPresenter);

    // Inject the presenter into the EvaluateTestView
    // (needs concrete type, not interface)
    evaluateTestView.setPresenter(originalEvalPresenter);

    return this;
  }

  /**
   * Adds the lecture notes view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addLectureNotesView() {
    this.lectureNotesViewModel =
        new interface_adapters.lecturenotes.LectureNotesViewModel();
    return this;
  }

  /**
   * Adds the lecture notes use case to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addLectureNotesUseCase() {
    // 1) gateway
    usecases.lecturenotes.CourseLookupGateway courseGateway =
        new data_access.LocalCourseLookupGateway(this.courseDao);

    // 2) presenter (wrapped with Timeline logging)
    interface_adapters.lecturenotes.GenerateLectureNotesPresenter originalPresenter =
        new interface_adapters.lecturenotes.GenerateLectureNotesPresenter(
            this.lectureNotesViewModel, this.viewManagerModel);

    // Wrap presenter to add Timeline logging
    usecases.lecturenotes.GenerateLectureNotesOutputBoundary presenter =
        new usecases.lecturenotes.GenerateLectureNotesOutputBoundary() {
          @Override
          public void prepareSuccessView(
              usecases.lecturenotes.GenerateLectureNotesOutputData outputData) {
            originalPresenter.prepareSuccessView(outputData);

            // Log to Timeline
            try {
              UUID courseUuid = CourseIdMapper.getUuidForCourseId(outputData.getCourseId());
              UUID contentId = UUID.randomUUID();
              String title = outputData.getTopic();
              String notesText = outputData.getNotesText();
              String snippet = notesText != null && notesText.length() > 100
                  ? notesText.substring(0, 100) + "..."
                  : notesText;
              timelineLogger.logNotesGenerated(
                  courseUuid, contentId, title, snippet, notesText);
            } catch (Exception e) {
              // Don't break the flow if Timeline logging fails
            }
          }

          @Override
          public void prepareFailView(String error) {
            originalPresenter.prepareFailView(error);
          }
        };

    // 3) interactor
    usecases.lecturenotes.GenerateLectureNotesInteractor interactor =
        new usecases.lecturenotes.GenerateLectureNotesInteractor(
            courseGateway, new data_access.NotesGeminiApiDataAccess(), presenter);

    // 4) controller
    interface_adapters.lecturenotes.GenerateLectureNotesController controller =
        new interface_adapters.lecturenotes.GenerateLectureNotesController(interactor);

    // 5) view (two-arg ctor)
    if (this.lectureNotesView == null) {
      this.lectureNotesView =
          new views.LectureNotesView(this.lectureNotesViewModel, controller);
      this.cardPanel.add(this.lectureNotesView, this.lectureNotesViewModel.getViewName());
    } else {
      // if the view already exists (hot rebuild path)
      this.lectureNotesView.setController(controller);
    }

    // 6) Back -> CourseWorkspace (fallback to Dashboard)
    this.lectureNotesView.setBackAction(() -> {
      if (this.courseWorkspaceView != null) {
        this.viewManagerModel.setState(this.courseWorkspaceView.getViewName());
      } else if (this.courseDashboardView != null) {
        this.viewManagerModel.setState(this.courseDashboardView.getViewName());
      }
      this.viewManagerModel.firePropertyChange();
    });

    return this;
  }

  // === SHIRLEY: Course dashboard / workspace methods ===

  /**
   * Adds the course dashboard view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addCourseDashboardView() {
    this.courseDashboardViewModel = new CourseDashboardViewModel();
    this.courseDashboardView = new CourseDashboardView(courseDashboardViewModel);
    cardPanel.add(courseDashboardView, courseDashboardView.getViewName());
    return this;
  }

  /**
   * Adds the course workspace view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addCourseWorkspaceView() {
    this.courseWorkspaceViewModel = new CourseWorkspaceViewModel();
    this.courseWorkspaceView = new CourseWorkspaceView(courseWorkspaceViewModel);
    this.courseWorkspaceView.setMockTestViewModel(this.mockTestViewModel);
    cardPanel.add(courseWorkspaceView, courseWorkspaceView.getViewName());
    return this;
  }

  /**
   * Adds the course create view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addCourseCreateView() {
    this.courseCreateViewModel = new CourseCreateViewModel();
    this.courseCreateView = new CourseCreateView(courseCreateViewModel);
    cardPanel.add(courseCreateView, courseCreateView.getViewName());
    return this;
  }

  /**
   * Adds the course edit view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addCourseEditView() {
    this.courseEditViewModel = new CourseEditViewModel();
    this.courseEditView = new CourseEditView(courseEditViewModel);
    cardPanel.add(courseEditView, courseEditView.getViewName());
    return this;
  }

  /**
   * Adds the file management view to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addFileManagementView() {
    this.fileManagementViewModel = new FileManagementViewModel();
    this.fileManagementView = new FileManagementView(fileManagementViewModel);
    cardPanel.add(fileManagementView, fileManagementView.getViewName());
    return this;
  }

  /**
   * Adds the course use cases to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addCourseUseCases() {
    // presenter for dashboard + navigation
    CourseDashboardOutputBoundary courseDashboardPresenter =
        new CourseDashboardPresenter(
            viewManagerModel,
            courseDashboardViewModel,
            courseWorkspaceViewModel,
            courseCreateViewModel);

    // local course repository for your use cases
    ICourseRepository courseRepository = this.courseDao;

    CourseDashboardInputBoundary courseDashboardInteractor =
        new CourseDashboardInteractor(courseRepository, courseDashboardPresenter);
    CourseDashboardController courseDashboardController =
        new CourseDashboardController(courseDashboardInteractor);

    // presenter for workspace / edit views
    CourseWorkspaceOutputBoundary coursePresenter =
        new CoursePresenter(
            viewManagerModel,
            courseDashboardViewModel,
            courseWorkspaceViewModel,
            courseEditViewModel);

    CourseWorkspaceInputBoundary courseWorkspaceInteractor =
        new CourseWorkspaceInteractor(
            courseRepository, coursePresenter, courseDashboardPresenter);
    CourseController courseController = new CourseController(courseWorkspaceInteractor);

    // hook controllers into your views
    this.courseDashboardView.setCourseDashboardController(courseDashboardController);
    this.courseDashboardView.setCourseWorkspaceController(courseController);

    this.courseWorkspaceView.setCourseDashboardController(courseDashboardController);
    this.courseWorkspaceView.setCourseWorkspaceController(courseController);

    // Open notes from the workspace; pass the current course id, then navigate.
    this.courseWorkspaceView.setOpenLectureNotesAction(() -> {
      // get currently selected course from the workspace VM
      var wsState = this.courseWorkspaceViewModel.getState();
      var course = (wsState == null) ? null : wsState.getCourse();
      String id = (course == null) ? "" : course.getCourseId();

      // hand id to notes view
      this.lectureNotesView.setCourseId(id);

      // go to notes screen
      this.viewManagerModel.setState(this.lectureNotesViewModel.getViewName());
      this.viewManagerModel.firePropertyChange();
    });

    // Open Timeline/History from the workspace;
    // convert course ID to UUID and navigate.
    // Initialize Timeline components lazily if not already initialized
    this.courseWorkspaceView.setOpenTimelineAction(() -> {
      // Initialize Timeline if not already done (lazy initialization)
      if (this.timelineViewModel == null) {
        this.addTimelineView();
      }

      // get currently selected course from the workspace VM
      var wsState = this.courseWorkspaceViewModel.getState();
      var course = (wsState == null) ? null : wsState.getCourse();
      String courseId = (course == null) ? "" : course.getCourseId();

      if (courseId != null && !courseId.isEmpty()) {
        // Convert String course ID to UUID using CourseIdMapper
        UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseId);

        // Set the course ID in the Timeline ViewModel
        this.timelineViewModel.setCourseId(courseUuid);

        // Load the timeline for this course
        this.timelineController.open(courseUuid);

        // Navigate to Timeline view
        this.viewManagerModel.setState(this.timelineViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
      }
    });

    this.courseCreateView.setCourseDashboardController(courseDashboardController);
    this.courseCreateView.setCourseWorkspaceController(courseController);

    this.courseEditView.setCourseWorkspaceController(courseController);

    // File management use case
    FileManagementOutputBoundary fileManagementPresenter =
        new FileManagementPresenter(fileManagementViewModel, viewManagerModel);

    FileManagementInputBoundary fileManagementInteractor =
        new FileManagementInteractor(courseRepository, fileManagementPresenter);
    FileManagementController fileManagementController =
        new FileManagementController(fileManagementInteractor);

    // Hook file management controller into views
    this.courseWorkspaceView.setFileManagementController(fileManagementController);
    this.courseWorkspaceView.setViewManagerModel(viewManagerModel);
    this.fileManagementView.setController(fileManagementController);
    this.fileManagementView.setViewManagerModel(viewManagerModel);

    return this;
  }

  // === FLASHCARD: Flashcard methods ===

  /**
   * Adds the flashcard generation and display views to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addFlashcardViews() {
    this.flashcardViewModel = new FlashcardViewModel();

    this.generateFlashcardsView = new GenerateFlashcardsView(flashcardViewModel);
    cardPanel.add(generateFlashcardsView, generateFlashcardsView.getViewName());

    this.flashcardDisplayView = new FlashcardDisplayView(flashcardViewModel);
    this.flashcardDisplayView.setViewManagerModel(viewManagerModel);
    cardPanel.add(flashcardDisplayView, flashcardDisplayView.getViewName());

    return this;
  }

  /**
   * Wires up the flashcard generation use case with all necessary dependencies.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addFlashcardGenerationUseCase() {
    // Create the flashcard generator (using Gemini API)
    FlashcardGenerator generator = getGeminiDao();

    // Create the original presenter
    GenerateFlashcardsPresenter originalPresenter =
        new GenerateFlashcardsPresenter(flashcardViewModel, viewManagerModel);

    // Wrap presenter to add Timeline logging
    // Use a final array to capture courseName from the interactor wrapper
    final String[] capturedCourseName = new String[1];

    GenerateFlashcardsOutputBoundary presenter =
        new GenerateFlashcardsOutputBoundary() {
          @Override
          public void presentFlashcards(GenerateFlashcardsResponseModel responseModel) {
            originalPresenter.presentFlashcards(responseModel);

            // Log to Timeline
            try {
              if (capturedCourseName[0] != null && !capturedCourseName[0].isEmpty()) {
                UUID courseUuid = CourseIdMapper.getUuidForCourseId(capturedCourseName[0]);
                UUID contentId = UUID.randomUUID();
                entities.FlashcardSet set = responseModel.getFlashcardSet();
                int numCards = set != null ? set.size() : 0;
                timelineLogger.logFlashcardsGenerated(courseUuid, contentId, numCards, set);
              }
            } catch (Exception e) {
              // Don't break the flow if Timeline logging fails
            }
          }

          @Override
          public void presentError(String message) {
            originalPresenter.presentError(message);
          }
        };

    // Wrap interactor to capture courseName
    GenerateFlashcardsInputBoundary interactor =
        new GenerateFlashcardsInputBoundary() {
          @Override
          public void execute(String courseName, String content) {
            capturedCourseName[0] = courseName;
            new GenerateFlashcardsInteractor(generator, presenter)
                .execute(courseName, content);
          }
        };

    GenerateFlashcardsController controller =
        new GenerateFlashcardsController(interactor);

    generateFlashcardsView.setController(controller);
    courseWorkspaceView.setFlashcardsController(controller);

    return this;
  }

  // === TIMELINE: Timeline methods ===

  /**
   * Adds the Timeline view and its sub-views (Notes, Flashcards, Quiz)
   * to the application.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addTimelineView() {
    // Create Timeline ViewModel
    this.timelineViewModel = new ViewTimelineViewModel();

    // Ensure required ViewModels exist
    // (they should be created by other add methods)
    if (this.lectureNotesViewModel == null) {
      this.lectureNotesViewModel =
          new interface_adapters.lecturenotes.LectureNotesViewModel();
    }
    if (this.flashcardViewModel == null) {
      this.flashcardViewModel = new FlashcardViewModel();
    }
    if (this.evaluateTestViewModel == null) {
      this.evaluateTestViewModel = new EvaluateTestViewModel();
    }
    if (this.mockTestViewModel == null) {
      this.mockTestViewModel = new MockTestViewModel();
    }

    // Create the presenter
    ViewTimelineSwingPresenter presenter =
        new ViewTimelineSwingPresenter(timelineViewModel);

    // Create the interactor
    ViewTimelineInteractor interactor =
        new ViewTimelineInteractor(timelineRepository, presenter);

    // Create the controller
    this.timelineController = new TimelineController(interactor);

    // Create Timeline View - pass ViewModels instead of simple views
    this.timelineView = new ViewTimelineView(
        timelineViewModel,
        timelineController,
        viewManagerModel,
        lectureNotesViewModel,
        flashcardViewModel,
        evaluateTestViewModel,
        mockTestViewModel);

    // Add Timeline view to cardPanel
    cardPanel.add(timelineView, timelineViewModel.getViewName());

    return this;
  }

  /**
   * Wires up the Timeline use case with all necessary dependencies.
   * This method is called after addTimelineView() to ensure proper
   * initialization order. Currently, all wiring is done in
   * addTimelineView(), but this method exists for consistency with
   * other features and potential future enhancements.
   *
   * @return this AppBuilder for method chaining
   */
  public AppBuilder addTimelineUseCase() {
    // All wiring is already done in addTimelineView()
    // This method exists for consistency with other features
    // and can be used for additional wiring if needed in the future

    return this;
  }

  /**
   * Gets the TimelineLogger instance for other use cases to log timeline events.
   *
   * @return The TimelineLogger instance
   */
  public TimelineLogger getTimelineLogger() {
    return timelineLogger;
  }

  /**
   * Builds and returns the complete JFrame application.
   *
   * @return the configured JFrame application
   */
  public JFrame build() {
    JFrame application = new JFrame("StudyFlow AI Assistant");
    application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    application.add(cardPanel);

    // Make CardLayout follow the ViewManagerModel state
    viewManagerModel.addPropertyChangeListener(evt -> {
      if (!"state".equals(evt.getPropertyName())) {
        return;
      }
      String targetView = (String) evt.getNewValue();
      if (targetView == null || targetView.isEmpty()) {
        return;
      }
      cardLayout.show(cardPanel, targetView);
      cardPanel.revalidate();
      cardPanel.repaint();
    });

    // Render dashboard and show it as the initial view
    this.courseDashboardView.renderDashboard();
    viewManagerModel.setState(this.courseDashboardView.getViewName());
    viewManagerModel.firePropertyChange();

    return application;
  }
}
