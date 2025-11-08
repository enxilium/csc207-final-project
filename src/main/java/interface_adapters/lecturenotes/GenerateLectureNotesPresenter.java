package interface_adapters.lecturenotes;

import interface_adapters.ViewManagerModel;
import usecases.lecturenotes.GenerateLectureNotesOutputBoundary;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

/**
 * Presenter for the lecture notes use case.
 * Converts OutputData to ViewModel state and tells ViewManager which view to show.
 */
public class GenerateLectureNotesPresenter implements GenerateLectureNotesOutputBoundary {

    private final LectureNotesViewModel lectureNotesViewModel;
    private final ViewManagerModel viewManagerModel;

    public GenerateLectureNotesPresenter(LectureNotesViewModel lectureNotesViewModel,
                                         ViewManagerModel viewManagerModel) {
        this.lectureNotesViewModel = lectureNotesViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(GenerateLectureNotesOutputData outputData) {
        LectureNotesState state = lectureNotesViewModel.getState();

        state.setCourseId(outputData.getCourseId());
        state.setTopic(outputData.getTopic());
        state.setNotesText(outputData.getNotesText());
        state.setError("");
        state.setLoading(false);

        lectureNotesViewModel.setState(state);
        // notify the view that notes have changed
        lectureNotesViewModel.firePropertyChange("notes");

        // switch to the lecture-notes view
        viewManagerModel.setState(LectureNotesViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LectureNotesState state = lectureNotesViewModel.getState();

        state.setError(errorMessage);
        state.setLoading(false);

        lectureNotesViewModel.setState(state);
        lectureNotesViewModel.firePropertyChange("error");
    }
}
