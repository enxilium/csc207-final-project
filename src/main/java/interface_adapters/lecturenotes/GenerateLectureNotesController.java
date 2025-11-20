package interface_adapters.lecturenotes;

import usecases.lecturenotes.GenerateLectureNotesInputBoundary;
import usecases.lecturenotes.GenerateLectureNotesInputData;

public class GenerateLectureNotesController {
    private final GenerateLectureNotesInputBoundary interactor;

    public GenerateLectureNotesController(GenerateLectureNotesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String courseId, String topic) {
        interactor.execute(new GenerateLectureNotesInputData(courseId, topic));
    }
}