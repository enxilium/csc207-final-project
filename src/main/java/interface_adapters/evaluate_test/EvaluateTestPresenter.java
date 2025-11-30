package interface_adapters.evaluate_test;

import usecases.Timeline.CourseIdMapper;
import usecases.Timeline.TimelineLogger;
import interface_adapters.LoadingViewModel;
import interface_adapters.ViewManagerModel;
import interface_adapters.dashboard.CourseDashboardViewModel;
import interface_adapters.mock_test.MockTestState;
import interface_adapters.mock_test.MockTestViewModel;
import usecases.evaluate_test.EvaluateTestOutputBoundary;
import usecases.evaluate_test.EvaluateTestOutputData;

import java.util.UUID;

public class EvaluateTestPresenter implements EvaluateTestOutputBoundary {
    private final EvaluateTestViewModel evaluateTestViewModel;
    private final LoadingViewModel loadingViewModel;
    private final CourseDashboardViewModel dashboardViewModel;
    private final ViewManagerModel viewManagerModel;
    private final TimelineLogger timelineLogger;
    private final MockTestViewModel mockTestViewModel;

    public EvaluateTestPresenter(EvaluateTestViewModel evaluateTestViewModel, LoadingViewModel loadingViewModel,
                                 CourseDashboardViewModel courseDashboardViewModel, ViewManagerModel viewManagerModel,
                                 TimelineLogger timelineLogger, MockTestViewModel mockTestViewModel) {
        this.evaluateTestViewModel = evaluateTestViewModel;
        this.loadingViewModel = loadingViewModel;
        this.dashboardViewModel = courseDashboardViewModel;
        this.viewManagerModel = viewManagerModel;
        this.timelineLogger = timelineLogger;
        this.mockTestViewModel = mockTestViewModel;
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

        // Log quiz submission to Timeline
        if (timelineLogger != null && mockTestViewModel != null) {
            try {
                MockTestState mockTestState = mockTestViewModel.getState();
                String courseId = mockTestState.getCourseId();
                if (courseId != null && !courseId.isEmpty()) {
                    UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseId);
                    UUID contentId = UUID.randomUUID(); // Generate a unique content ID for this submission
                    int numQuestions = evaluateTestOutputData.getQuestions() != null 
                        ? evaluateTestOutputData.getQuestions().size() : 0;
                    double score = evaluateTestOutputData.getScore();
                    timelineLogger.logQuizSubmitted(courseUuid, contentId, numQuestions, score, evaluateTestOutputData);
                }
            } catch (Exception e) {
                // Log error but don't break the flow
                System.err.println("Failed to log quiz submission to timeline: " + e.getMessage());
            }
        }
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

    public void presentDashboard() {
        // Switch back to dashboard
        this.viewManagerModel.setState(this.dashboardViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
