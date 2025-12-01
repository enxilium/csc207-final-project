package interface_adapters.dashboard;

import interface_adapters.ViewManagerModel;
import usecases.*;
import usecases.dashboard.*;
import usecases.workspace.*;
import interface_adapters.workspace.*;
import java.util.List;

/**
 * Presenter for the course dashboard use case.
 * Prepares view models and manages view transitions for the dashboard.
 */
public class CourseDashboardPresenter implements CourseDashboardOutputBoundary {
    private final CourseDashboardViewModel courseDashboardViewModel;
    private final CourseWorkspaceViewModel courseWorkspaceViewModel;
    private final CourseCreateViewModel courseCreateViewModel;

    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs a CourseDashboardPresenter with the given view models.
     *
     * @param viewManagerModel the model for managing view transitions
     * @param courseDashboardViewModel the view model for the dashboard
     * @param courseWorkspaceViewModel the view model for the workspace
     * @param courseCreateViewModel the view model for course creation
     */
    public CourseDashboardPresenter(ViewManagerModel viewManagerModel,
                                    CourseDashboardViewModel  courseDashboardViewModel,
                                    CourseWorkspaceViewModel  courseWorkspaceViewModel,
                                    CourseCreateViewModel  courseCreateViewModel){
        this.viewManagerModel = viewManagerModel;
        this.courseDashboardViewModel = courseDashboardViewModel;
        this.courseWorkspaceViewModel = courseWorkspaceViewModel;
        this.courseCreateViewModel = courseCreateViewModel;
    }

    @Override
    public void prepareDashboardView(CourseDashboardOutputData response){
        final CourseDashboardState courseDashboardState = courseDashboardViewModel.getState();
        courseDashboardState.setCourses(response.getCourses());
        this.courseDashboardViewModel.firePropertyChange();

        // and clear everything from the dashboard's state
        this.courseWorkspaceViewModel.setState(new CourseState());

        this.viewManagerModel.setState(courseDashboardViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
    @Override
    public void prepareFailView(String errorMessage){
        final CourseDashboardState courseDashboardState = courseDashboardViewModel.getState();
        courseDashboardState.setError(errorMessage);
        this.courseDashboardViewModel.firePropertyChange();
    }

    @Override
    public void prepareCreateCourseView(){
        final CourseState courseWorkspaceState = courseCreateViewModel.getState();
        this.courseCreateViewModel.firePropertyChange();

        // and clear everything from the dashboard's state
        this.courseDashboardViewModel.setState(new CourseDashboardState());

        this.viewManagerModel.setState(courseCreateViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}

