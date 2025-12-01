package interface_adapters.evaluate_test;

import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.dashboard.CourseDashboardViewModel;
import usecases.evaluate_test.EvaluateTestOutputBoundary;
import usecases.evaluate_test.EvaluateTestOutputData;

/**
 * Presenter for the evaluate test use case.
 */
public class EvaluateTestPresenter implements EvaluateTestOutputBoundary {
  private final EvaluateTestViewModel evaluateTestViewModel;
  private final LoadingViewModel loadingViewModel;
  private final CourseDashboardViewModel dashboardViewModel;
  private final ViewManagerModel viewManagerModel;

  /**
   * Constructs an EvaluateTestPresenter with the given view models.
   *
   * @param evaluateTestViewModel the view model for test evaluation
   * @param loadingViewModel the view model for loading state
   * @param courseDashboardViewModel the view model for the dashboard
   * @param viewManagerModel the model for managing view transitions
   */
  public EvaluateTestPresenter(EvaluateTestViewModel evaluateTestViewModel,
      LoadingViewModel loadingViewModel,
      CourseDashboardViewModel courseDashboardViewModel,
      ViewManagerModel viewManagerModel) {
    this.evaluateTestViewModel = evaluateTestViewModel;
    this.loadingViewModel = loadingViewModel;
    this.dashboardViewModel = courseDashboardViewModel;
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
    // TODO: Maybe create a more sophisticated error handling screen
    System.out.println("Error: " + errorMessage);
  }

  /**
   * Navigates to the next question.
   */
  public void goToNextQuestion() {
    final EvaluateTestState evaluateTestState = evaluateTestViewModel.getState();
    int currentQuestionIndex = evaluateTestState.getCurrentQuestionIndex();
    evaluateTestState.setCurrentQuestionIndex(currentQuestionIndex + 1);

    evaluateTestViewModel.firePropertyChange();
  }

  /**
   * Navigates to the previous question.
   */
  public void goToPreviousQuestion() {
    final EvaluateTestState evaluateTestState = evaluateTestViewModel.getState();
    int currentQuestionIndex = evaluateTestState.getCurrentQuestionIndex();
    evaluateTestState.setCurrentQuestionIndex(currentQuestionIndex - 1);

    evaluateTestViewModel.firePropertyChange();
  }

  /**
   * Navigates back to the dashboard.
   */
  public void presentDashboard() {
    // Switch back to dashboard
    this.viewManagerModel.setState(this.dashboardViewModel.getViewName());
    this.viewManagerModel.firePropertyChange();
  }
}
