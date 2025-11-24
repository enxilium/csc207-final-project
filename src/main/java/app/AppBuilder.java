package app;

import data_access.DemoCourseAccess;
import data_access.GeminiApiDataAccess;
import entities.Course;
import entities.PDFFile;
import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.evaluate_test.*;
import interface_adapters.mock_test.*;
import usecases.evaluate_test.EvaluateTestInteractor;
import usecases.mock_test_generation.MockTestGenerationInteractor;

import views.*;

import javax.swing.*;
import java.awt.*;

// === SHIRLEY: Course dashboard / workspace imports ===
import interface_adapters.dashboard.*;
import interface_adapters.workspace.*;
import usecases.*;
import usecases.dashboard.*;
import usecases.workspace.*;
import data_access.*;

public class AppBuilder {
    // --- Shared Components held by the Builder ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // --- Data Access Objects ---
    private LocalCourseRepository courseDAO = new LocalCourseRepository();
    private final GeminiApiDataAccess geminiDAO = new GeminiApiDataAccess();

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

    public AppBuilder() {
        PDFFile dummyPdf = new PDFFile("test.pdf");
        Course dummyCourse = new Course("PHL245", "Modern Symbolic Logic", "demo course");
        dummyCourse.addFile(dummyPdf);
        courseDAO.create(dummyCourse);
    }


    public AppBuilder addWriteTestView() {
        mockTestViewModel = new MockTestViewModel();
        writeTestView = new WriteTestView(mockTestViewModel); // The view for taking the test
        cardPanel.add(writeTestView, mockTestViewModel.getViewName());
        return this;
    }

    public AppBuilder addEvaluateTestView() {
        evaluateTestViewModel = new EvaluateTestViewModel();
        evaluateTestView = new EvaluateTestView(evaluateTestViewModel);
        cardPanel.add(evaluateTestView, evaluateTestViewModel.getViewName());
        return this;
    }

    public AppBuilder addLoadingView() {
        loadingViewModel = new LoadingViewModel();
        LoadingView loadingView = new LoadingView(loadingViewModel);
        cardPanel.add(loadingView, loadingViewModel.getViewName());
        return this;
    }

    public AppBuilder addMockTestGenerationUseCase() {
        MockTestPresenter presenter = new MockTestPresenter(mockTestViewModel, viewManagerModel, loadingViewModel);
        MockTestGenerationInteractor interactor = new MockTestGenerationInteractor(courseDAO, geminiDAO, presenter);
        MockTestController controller = new MockTestController(interactor);
        this.courseWorkspaceView.setMockTestController(controller);

        return this;
    }

    public AppBuilder addEvaluateTestUseCase() {
        // The Presenter for the evaluation results view
        EvaluateTestPresenter evalPresenter = new EvaluateTestPresenter(evaluateTestViewModel, loadingViewModel, viewManagerModel);

        // The Interactor for the evaluation use case. It correctly uses the DAOs.
        EvaluateTestInteractor evalInteractor = new EvaluateTestInteractor(courseDAO, geminiDAO, evalPresenter);

        // The Controller that the WriteTestView will use to trigger the evaluation.
        EvaluateTestController evalController = new EvaluateTestController(evalInteractor);

        // The Presenter for the WriteTestView's navigation (next/prev question).
        MockTestPresenter mockTestPresenter = new MockTestPresenter(mockTestViewModel, viewManagerModel, loadingViewModel);

        // Inject both the controller (for submitting) and the presenter (for navigation) into the WriteTestView.
        writeTestView.setController(evalController);
        writeTestView.setPresenter(mockTestPresenter);

        // Inject the presenter into the EvaluateTestView
        evaluateTestView.setPresenter(evalPresenter);

        return this;
    }

    public AppBuilder addLectureNotesView() {
        this.lectureNotesViewModel = new interface_adapters.lecturenotes.LectureNotesViewModel();
        return this;
    }

    public AppBuilder addLectureNotesUseCase() {
        // 1) gateway
        usecases.lecturenotes.CourseLookupGateway courseGateway =
                new data_access.HardCodedCourseLookup();

        // 2) presenter
        interface_adapters.lecturenotes.GenerateLectureNotesPresenter presenter =
                new interface_adapters.lecturenotes.GenerateLectureNotesPresenter(
                        this.lectureNotesViewModel, this.viewManagerModel);

        // 3) interactor
        usecases.lecturenotes.GenerateLectureNotesInteractor interactor =
                new usecases.lecturenotes.GenerateLectureNotesInteractor(
                        courseGateway,
                        new data_access.NotesGeminiApiDataAccess(),
                        presenter
                );

        // 4) controller
        interface_adapters.lecturenotes.GenerateLectureNotesController controller =
                new interface_adapters.lecturenotes.GenerateLectureNotesController(interactor);

        // 5) view (two-arg ctor)
        if (this.lectureNotesView == null) {
            this.lectureNotesView =
                    new views.LectureNotesView(this.lectureNotesViewModel, controller);
            this.cardPanel.add(this.lectureNotesView,
                    this.lectureNotesViewModel.getViewName());
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

    public AppBuilder addCourseDashboardView() {
        this.courseDashboardViewModel = new CourseDashboardViewModel();
        this.courseDashboardView = new CourseDashboardView(courseDashboardViewModel);
        cardPanel.add(courseDashboardView, courseDashboardView.getViewName());
        return this;
    }

    public AppBuilder addCourseWorkspaceView() {
        this.courseWorkspaceViewModel = new CourseWorkspaceViewModel();
        this.courseWorkspaceView = new CourseWorkspaceView(courseWorkspaceViewModel);
        cardPanel.add(courseWorkspaceView, courseWorkspaceView.getViewName());
        return this;
    }

    public AppBuilder addCourseCreateView() {
        this.courseCreateViewModel = new CourseCreateViewModel();
        this.courseCreateView = new CourseCreateView(courseCreateViewModel);
        cardPanel.add(courseCreateView, courseCreateView.getViewName());
        return this;
    }

    public AppBuilder addCourseEditView() {
        this.courseEditViewModel = new CourseEditViewModel();
        this.courseEditView = new CourseEditView(courseEditViewModel);
        cardPanel.add(courseEditView, courseEditView.getViewName());
        return this;
    }

    public AppBuilder addCourseUseCases() {
        // presenter for dashboard + navigation
        CourseDashboardOutputBoundary courseDashboardPresenter =
                new CourseDashboardPresenter(
                        viewManagerModel,
                        courseDashboardViewModel,
                        courseWorkspaceViewModel,
                        courseCreateViewModel
                );

        // local course repository for your use cases
        ICourseRepository courseRepository = new LocalCourseRepository();

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
                        courseEditViewModel
                );

        CourseWorkspaceInputBoundary courseWorkspaceInteractor =
                new CourseWorkspaceInteractor(courseRepository, coursePresenter, courseDashboardPresenter);
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

        this.courseCreateView.setCourseDashboardController(courseDashboardController);
        this.courseCreateView.setCourseWorkspaceController(courseController);

        this.courseEditView.setCourseWorkspaceController(courseController);

        return this;
    }

    public JFrame build() {
        JFrame application = new JFrame("StudyFlow AI Assistant");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);
        this.courseDashboardView.renderDashboard();


        // Set the initial view
        viewManagerModel.setState(this.courseDashboardView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}