package interface_adapters.lecturenotes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for lecture notes generation and display.
 */
public class LectureNotesViewModel {
  private static final String VIEW_NAME = "lecture_notes";
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private LectureNotesState state = new LectureNotesState();

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return VIEW_NAME;
  }

  /**
   * Gets the current state.
   *
   * @return the current state
   */
  public LectureNotesState getState() {
    return state;
  }

  /**
   * Sets the state and fires a property change event.
   *
   * @param newState the new state
   */
  public void setState(LectureNotesState newState) {
    this.state = newState;
    firePropertyChange();
  }

  /**
   * Fires a property change event for the state.
   */
  public void firePropertyChange() {
    pcs.firePropertyChange("state", null, state);
  }

  /**
   * Adds a property change listener.
   *
   * @param l the listener to add
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  /**
   * Removes a property change listener.
   *
   * @param l the listener to remove
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }
}
