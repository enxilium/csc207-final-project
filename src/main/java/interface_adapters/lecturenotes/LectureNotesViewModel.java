package interface_adapters.lecturenotes;

import interface_adapters.ViewModel;

/**
 * ViewModel wrapping LectureNotesState.
 * ViewManager will use VIEW_NAME to switch to this view.
 */
public class LectureNotesViewModel extends ViewModel<LectureNotesState> {

    public static final String VIEW_NAME = "lecture notes";

    public LectureNotesViewModel() {
        super(VIEW_NAME);
        // start with an empty state
        this.setState(new LectureNotesState());
    }
}
