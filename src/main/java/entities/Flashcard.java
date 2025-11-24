package entities;

public class Flashcard {
    private final String question;
    private final String answer;

    // Optional new fields for front/back (synonyms for question/answer)
    private final String front;
    private final String back;

    // Constructor for normal question-answer card
    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.front = question;
        this.back = answer;
    }

    // Original getters remain untouched
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    // New optional getters (for systems that prefer front/back naming)
    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

    @Override
    public String toString() {
        return "Q: " + question + " | A: " + answer;
    }
}
