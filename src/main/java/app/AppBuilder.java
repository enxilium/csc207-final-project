package app;

import data_access.GeminiApiDataAccess;
import entities.Course;
import entities.PDFFile;
import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.evaluate_test.*;
import interface_adapters.mock_test.*;
import interface_adapters.lecturenotes.GenerateLectureNotesController;
import interface_adapters.lecturenotes.GenerateLectureNotesPresenter;
import interface_adapters.lecturenotes.LectureNotesViewModel;

import usecases.evaluate_test.EvaluateTestInteractor;
import usecases.mock_test_generation.MockTestGenerationInteractor;
import usecases.lecturenotes.CourseLookupGateway;
import usecases.lecturenotes.GenerateLectureNotesInteractor;

import views.*;
import views.ViewManager;
import views.LectureNotesView;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

public class AppBuilder {
    // --- Shared Components held by the Builder ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // --- Data Access Objects ---
    // TODO: Add proper Course management data access implementation (file-based)
    private Object courseDAO = null;
    private final GeminiApiDataAccess geminiDAO = new GeminiApiDataAccess();

    // --- ViewModels and Views (stored for wiring) ---
    private MockTestViewModel mockTestViewModel;
    private EvaluateTestViewModel evaluateTestViewModel;
    private LoadingViewModel loadingViewModel;
    private LectureNotesViewModel lectureNotesViewModel;
    private WriteTestView writeTestView;
    private EvaluateTestView evaluateTestView;
    private LectureNotesView lectureNotesView;

    public AppBuilder() {
    }


    public AppBuilder addWriteTestView() {
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

    public AppBuilder addLectureNotesView() {
        lectureNotesViewModel = new LectureNotesViewModel();
        lectureNotesView = new LectureNotesView(lectureNotesViewModel);
        cardPanel.add(lectureNotesView, lectureNotesViewModel.getViewName());
        return this;
    }

    public AppBuilder addMockTestGenerationUseCase() {
        MockTestPresenter presenter = new MockTestPresenter(mockTestViewModel, viewManagerModel, loadingViewModel);
        MockTestGenerationInteractor interactor = new MockTestGenerationInteractor(courseDAO, geminiDAO, presenter);
        MockTestController controller = new MockTestController(interactor);
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

    public AppBuilder addLectureNotesUseCase() {
        // 1. Course gateway (temporary hard-coded implementation for local testing)
        CourseLookupGateway courseGateway = new HardCodedCourseLookup();

        // 2. Presenter: updates the LectureNotesViewModel and switches the active view
        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(lectureNotesViewModel, viewManagerModel);

        // 3. Interactor: core use case logic (uses courseGateway and geminiDAO)
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, geminiDAO, presenter);

        // 4. Controller: called by the LectureNotesView
        GenerateLectureNotesController controller =
                new GenerateLectureNotesController(interactor);

        // 5. Inject controller into the LectureNotesView
        lectureNotesView.setController(controller);

        return this;
    }

    public JFrame build() {
        JFrame application = new JFrame("StudyFlow AI Assistant");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        // Set the initial view
        viewManagerModel.setState("demo view");
        viewManagerModel.firePropertyChange();

        return application;
    }
}