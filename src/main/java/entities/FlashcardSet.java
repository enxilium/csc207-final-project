package entities;

import java.util.List;

public class FlashcardSet {
    private final String courseName;
    private final List<Flashcard> flashcards;

    // Constructor — allows creating a set with a course name and a list of flashcards
    public FlashcardSet(String courseName, List<Flashcard> flashcards) {
        this.courseName = courseName;
        this.flashcards = flashcards;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    // Helper methods for extended usage
    public int size() {
        return flashcards.size();
    }

    public Flashcard getCard(int index) {
        return flashcards.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flashcard Set for Course: ").append(courseName).append("\n");
        for (Flashcard f : flashcards) {
            sb.append(" - ").append(f.toString()).append("\n");
        }
        return sb.toString();
    }
}