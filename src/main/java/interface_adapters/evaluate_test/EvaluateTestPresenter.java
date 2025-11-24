package interface_adapters.evaluate_test;

import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.mock_test.MockTestState;
import interface_adapters.mock_test.MockTestViewModel;
import usecases.evaluate_test.EvaluateTestOutputBoundary;
import usecases.evaluate_test.EvaluateTestOutputData;

public class EvaluateTestPresenter implements EvaluateTestOutputBoundary {
    private final EvaluateTestViewModel evaluateTestViewModel;
    private final LoadingViewModel loadingViewModel;
    private final ViewManagerModel viewManagerModel;

    public EvaluateTestPresenter(EvaluateTestViewModel evaluateTestViewModel, LoadingViewModel loadingViewModel,
                                 ViewManagerModel viewManagerModel) {
        this.evaluateTestViewModel = evaluateTestViewModel;
        this.loadingViewModel = loadingViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentEvaluationResults(EvaluateTestOutputData evaluateTestOutputData) {
        final EvaluateTestState evaluateTestState = evaluateTestViewModel.getState();

        evaluateTestState.setCorrectness(evaluateTestOutputData.getCorrectness());
        evaluateTestState.setScore(evaluateTestOutputData.getScore());
        evaluateTestState.setQuestions(evaluateTestOutputData.getQuestions());
        evaluateTestState.setAnswers(evaluateTestOutputData.getAnswers());
        evaluateTestState.setUserAnswers(evaluateTestOutputData.getUserAnswers());
        evaluateTestState.setFeedback(evaluateTestOutputData.getFeedback());

        evaluateTestViewModel.firePropertyChange();

        this.viewManagerModel.setState(this.evaluateTestViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentLoading() {
        // Switch to loading view
        this.viewManagerModel.setState(this.loadingViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentError(String errorMessage) {
        System.out.println("Error: " + errorMessage); // TODO: Maybe create a more sophisticated error handling screen
    }

    public void goToNextQuestion() {
        final EvaluateTestState evaluateTestState = evaluateTestViewModel.getState();
        int currentQuestionIndex = evaluateTestState.getCurrentQuestionIndex();
        evaluateTestState.setCurrentQuestionIndex(currentQuestionIndex + 1);

        evaluateTestViewModel.firePropertyChange();
    }

    public void goToPreviousQuestion() {
        final EvaluateTestState evaluateTestState = evaluateTestViewModel.getState();
        int currentQuestionIndex = evaluateTestState.getCurrentQuestionIndex();
        evaluateTestState.setCurrentQuestionIndex(currentQuestionIndex - 1);

        evaluateTestViewModel.firePropertyChange();
    }
}
