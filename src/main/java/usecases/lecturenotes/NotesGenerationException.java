package usecases.lecturenotes;

public class NotesGenerationException extends Exception {

    public NotesGenerationException(String message) {
        super(message);
    }

    public NotesGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
