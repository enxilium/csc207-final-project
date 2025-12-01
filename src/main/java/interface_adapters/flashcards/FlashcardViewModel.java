package interface_adapters.flashcards;

import entities.FlashcardSet;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for flashcard generation and display.
 * Manages the state of flashcards and notifies views of changes.
 */
public class FlashcardViewModel {
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  private FlashcardSet currentFlashcardSet;
  private int currentCardIndex = 0;
  private boolean isFlipped = false;
  private String errorMessage = null;
  private boolean isLoading = false;

  public static final String FLASHCARDS_GENERATED = "flashcardsGenerated";
  public static final String ERROR_OCCURRED = "errorOccurred";
  public static final String CARD_CHANGED = "cardChanged";
  public static final String CARD_FLIPPED = "cardFlipped";
  public static final String LOADING_CHANGED = "loadingChanged";

  /**
   * Gets the current flashcard set.
   *
   * @return the current flashcard set
   */
  public FlashcardSet getCurrentFlashcardSet() {
    return currentFlashcardSet;
  }

  /**
   * Sets the current flashcard set and resets the card index and flip state.
   *
   * @param flashcardSet the flashcard set to set
   */
  public void setCurrentFlashcardSet(FlashcardSet flashcardSet) {
    this.currentFlashcardSet = flashcardSet;
    this.currentCardIndex = 0;
    this.isFlipped = false;
    support.firePropertyChange(FLASHCARDS_GENERATED, null, flashcardSet);
  }

  /**
   * Gets the current card index.
   *
   * @return the current card index
   */
  public int getCurrentCardIndex() {
    return currentCardIndex;
  }

  /**
   * Sets the current card index if valid.
   *
   * @param index the card index to set
   */
  public void setCurrentCardIndex(int index) {
    if (currentFlashcardSet != null && index >= 0
        && index < currentFlashcardSet.size()) {
      int oldIndex = this.currentCardIndex;
      this.currentCardIndex = index;
      this.isFlipped = false;
      support.firePropertyChange(CARD_CHANGED, oldIndex, index);
    }
  }

  /**
   * Checks if the current card is flipped.
   *
   * @return true if the card is flipped
   */
  public boolean isFlipped() {
    return isFlipped;
  }

  /**
   * Sets the flipped state of the current card.
   *
   * @param flipped whether the card is flipped
   */
  public void setFlipped(boolean flipped) {
    boolean oldValue = this.isFlipped;
    this.isFlipped = flipped;
    support.firePropertyChange(CARD_FLIPPED, oldValue, flipped);
  }

  /**
   * Gets the error message.
   *
   * @return the error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the error message.
   *
   * @param errorMessage the error message to set
   */
  public void setErrorMessage(String errorMessage) {
    String oldMessage = this.errorMessage;
    this.errorMessage = errorMessage;
    support.firePropertyChange(ERROR_OCCURRED, oldMessage, errorMessage);
  }

  /**
   * Checks if flashcards are currently loading.
   *
   * @return true if loading
   */
  public boolean isLoading() {
    return isLoading;
  }

  /**
   * Sets the loading state.
   *
   * @param loading whether flashcards are loading
   */
  public void setLoading(boolean loading) {
    boolean oldValue = this.isLoading;
    this.isLoading = loading;
    support.firePropertyChange(LOADING_CHANGED, oldValue, loading);
  }

  /**
   * Adds a property change listener.
   *
   * @param listener the listener to add
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  /**
   * Removes a property change listener.
   *
   * @param listener the listener to remove
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return "flashcard";
  }
}
