package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;

/**
 * This interface hides the details of calling the Gemini SDK.
 *
 * The lecture-notes interactor will call this, and the actual
 * implementation (e.g., data_access.GeminiApiDataAccess) will
 * use the Google Gemini Java SDK to generate notes from the
 * uploaded PDF files for a given course and topic.
 */
public interface NotesGeminiGateway {

    /**
     * Given a Course (with its uploaded PDF files) and a topic,
     * call Gemini to generate lecture notes, and return them as
     * a LectureNotes entity.
     *
     * @throws Exception if the Gemini call fails or something goes wrong.
     */
    LectureNotes generateNotes(Course course, String topic) throws Exception;
}