package interface_adapters.mock_test;

import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import usecases.mock_test_generation.MockTestGenerationOutputBoundary;
import usecases.mock_test_generation.MockTestGenerationOutputData;

import javax.swing.SwingUtilities;

public class MockTestPresenter implements MockTestGenerationOutputBoundary {

    private final MockTestViewModel mockTestViewModel;
    private final LoadingViewModel loadingViewModel;
    private final ViewManagerModel viewManagerModel;

    public MockTestPresenter(MockTestViewModel mockTestViewModel, ViewManagerModel viewManagerModel,
                             LoadingViewModel loadingViewModel) {
        this.mockTestViewModel = mockTestViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loadingViewModel = loadingViewModel;
    }

    public void presentTest(MockTestGenerationOutputData mockTestGenerationOutputData) {
        runOnEdt(() -> {
            final MockTestState mockTestState = mockTestViewModel.getState();
            mockTestState.setCourseId(mockTestGenerationOutputData.getCourseId());
            mockTestState.setQuestions(mockTestGenerationOutputData.getQuestions());
            mockTestState.setAnswers(mockTestGenerationOutputData.getAnswers());
            mockTestState.setQuestionTypes(mockTestGenerationOutputData.getQuestionTypes());
            mockTestState.setChoices(mockTestGenerationOutputData.getChoices());

            mockTestState.initializeUserAnswers(mockTestGenerationOutputData.getQuestions().size());
            mockTestState.setCurrentQuestionIndex(
                    mockTestState.getQuestions().isEmpty() ? -1 : 0);

            mockTestViewModel.firePropertyChange();

            // Switch to mockTest view
            viewManagerModel.setState(mockTestViewModel.getViewName());
            viewManagerModel.firePropertyChange();
        });
    }

    public void presentLoading() {
        runOnEdt(() -> {
            viewManagerModel.setState(loadingViewModel.getViewName());
            viewManagerModel.firePropertyChange();
        });
    }

    public void presentError(String errorMessage) {
        runOnEdt(() -> System.out.println("Error: " + errorMessage)); // TODO: Enhance error handling UI
    }

    public void goToNextQuestion() {
        final MockTestState mockTestState = mockTestViewModel.getState();
        int currentQuestionIndex = mockTestState.getCurrentQuestionIndex();
        int lastIndex = mockTestState.getQuestions().size() - 1;
        if (currentQuestionIndex >= 0 && currentQuestionIndex < lastIndex) {
            mockTestState.setCurrentQuestionIndex(currentQuestionIndex + 1);
            mockTestViewModel.firePropertyChange();
        }
    }

    public void goToPreviousQuestion() {
        final MockTestState mockTestState = mockTestViewModel.getState();
        int currentQuestionIndex = mockTestState.getCurrentQuestionIndex();
        if (currentQuestionIndex > 0) {
            mockTestState.setCurrentQuestionIndex(currentQuestionIndex - 1);
            mockTestViewModel.firePropertyChange();
        }
    }

    private void runOnEdt(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}
