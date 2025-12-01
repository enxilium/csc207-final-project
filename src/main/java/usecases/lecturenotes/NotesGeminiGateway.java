package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;

/**
 * This interface hides the details of calling the Gemini SDK.
 */
public interface NotesGeminiGateway {

    /**
     * Given a Course (with its uploaded PDF files) and a topic,
     * call Gemini to generate lecture notes, and return them as
     * a LectureNotes entity.
     *
     * @throws NotesGenerationException if the Gemini call or I/O fails.
     */
    LectureNotes generateNotes(Course course, String topic)
            throws NotesGenerationException;
}
