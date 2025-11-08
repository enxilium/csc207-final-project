package interface_adapters.lecturenotes;

import usecases.lecturenotes.GenerateLectureNotesInputBoundary;
import usecases.lecturenotes.GenerateLectureNotesInputData;

/**
 * Controller called by the Lecture Notes view when the user
 * wants to generate notes.
 */
public class GenerateLectureNotesController {

    private final GenerateLectureNotesInputBoundary interactor;

    public GenerateLectureNotesController(GenerateLectureNotesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void generate(String courseId, String topic) {
        GenerateLectureNotesInputData inputData =
                new GenerateLectureNotesInputData(courseId, topic);
        interactor.execute(inputData);
    }
}