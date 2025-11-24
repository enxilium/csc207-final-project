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

    public FlashcardSet getCurrentFlashcardSet() {
        return currentFlashcardSet;
    }

    public void setCurrentFlashcardSet(FlashcardSet flashcardSet) {
        this.currentFlashcardSet = flashcardSet;
        this.currentCardIndex = 0;
        this.isFlipped = false;
        support.firePropertyChange(FLASHCARDS_GENERATED, null, flashcardSet);
    }

    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    public void setCurrentCardIndex(int index) {
        if (currentFlashcardSet != null && index >= 0 && index < currentFlashcardSet.size()) {
            int oldIndex = this.currentCardIndex;
            this.currentCardIndex = index;
            this.isFlipped = false;
            support.firePropertyChange(CARD_CHANGED, oldIndex, index);
        }
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        boolean oldValue = this.isFlipped;
        this.isFlipped = flipped;
        support.firePropertyChange(CARD_FLIPPED, oldValue, flipped);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String oldMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange(ERROR_OCCURRED, oldMessage, errorMessage);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        boolean oldValue = this.isLoading;
        this.isLoading = loading;
        support.firePropertyChange(LOADING_CHANGED, oldValue, loading);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getViewName() {
        return "flashcard";
    }
}