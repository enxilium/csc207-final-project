package interface_adapters.workspace;

import usecases.*;
import interface_adapters.*;
import interface_adapters.dashboard.*;
import usecases.workspace.*;


public class CoursePresenter implements CourseWorkspaceOutputBoundary {
    private final CourseDashboardViewModel courseDashboardViewModel;
    private final CourseWorkspaceViewModel courseWorkspaceViewModel;
    private final ViewManagerModel viewManagerModel;
    private final CourseEditViewModel courseEditViewModel;

    public CoursePresenter(ViewManagerModel viewManagerModel,
                           CourseDashboardViewModel courseDashboardViewModel,
                           CourseWorkspaceViewModel  courseWorkspaceViewModel,
                           CourseEditViewModel  courseEditViewModel){
        this.viewManagerModel = viewManagerModel;
        this.courseDashboardViewModel = courseDashboardViewModel;
        this.courseWorkspaceViewModel = courseWorkspaceViewModel;
        this.courseEditViewModel = courseEditViewModel;
    }

    @Override
    public void prepareWorkspaceView(CourseWorkspaceOutputData response){
        final CourseState courseWorkspaceState = courseWorkspaceViewModel.getState();

        courseWorkspaceState.setCourse(response.getCourse());
        this.courseWorkspaceViewModel.firePropertyChange();

        // and clear everything from the dashboard's state
        this.courseDashboardViewModel.setState(new CourseDashboardState());

        // switch to the workspace in view
        this.viewManagerModel.setState(courseWorkspaceViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareEditView(CourseWorkspaceOutputData response){
        final CourseState courseWorkspaceState = courseEditViewModel.getState();

        courseWorkspaceState.setCourse(response.getCourse());
        this.courseEditViewModel.firePropertyChange();

        // switch to the workspace in view
        this.viewManagerModel.setState(courseEditViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }


    @Override
    public void prepareFailView(String errorMessage){

    }
}

