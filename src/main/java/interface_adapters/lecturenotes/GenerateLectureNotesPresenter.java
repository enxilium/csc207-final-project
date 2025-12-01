package interface_adapters.lecturenotes;

import interface_adapters.ViewManagerModel;
import usecases.lecturenotes.GenerateLectureNotesOutputBoundary;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

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
        viewManagerModel.setState(lectureNotesViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        LectureNotesState state = lectureNotesViewModel.getState();
        state.setError(error);
        state.setLoading(false);
        lectureNotesViewModel.setState(state);
    }
}