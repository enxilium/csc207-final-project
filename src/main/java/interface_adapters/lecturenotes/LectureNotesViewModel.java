package interface_adapters.lecturenotes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LectureNotesViewModel {
    private static final String VIEW_NAME = "lecture_notes";
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private LectureNotesState state = new LectureNotesState();

    public String getViewName() { return VIEW_NAME; }

    public LectureNotesState getState() { return state; }

    public void setState(LectureNotesState newState) {
        this.state = newState;
        firePropertyChange();
    }

    public void firePropertyChange() {
        pcs.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}