package app;

import data_access.GeminiFlashcardGenerator;
import data_access.HardCodedCourseLookup;
import data_access.DemoCourseAccess;
import data_access.GeminiApiDataAccess;
import entities.Course;
import entities.PDFFile;
import interface_adapters.flashcards.GenerateFlashcardsPresenter;
import usecases.GenerateFlashcardsInputBoundary;
import usecases.GenerateFlashcardsInteractor;

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

    // --- Data Access Objects ---
    private LocalCourseRepository courseDAO = new LocalCourseRepository();
    private final GeminiApiDataAccess geminiDAO = new GeminiApiDataAccess();

            if (course == null || course.getUploadedFiles().isEmpty()) {
                throw new IllegalStateException("No course or PDF found.");
            }

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

            // Load PDF from src/main/resources/
            ClassLoader cl = getClass().getClassLoader();
            URL fileUrl = cl.getResource(pdf.getPath().toString());

            if (fileUrl == null) {
                System.out.println("ERROR: PDF not found in resources folder.");
                return;
            }

            File file = new File(fileUrl.toURI());

            // Pass absolute path to Gemini generator
            String content = file.getAbsolutePath();


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

        } catch (Exception e) {
            System.err.println("Error running flashcard demo: " + e.getMessage());
        }
    }
}