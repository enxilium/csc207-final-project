package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;

/**
 * Use case interactor for generating lecture notes
 * based on uploaded PDF files and a topic.
 */
public class GenerateLectureNotesInteractor implements GenerateLectureNotesInputBoundary {

    private final CourseLookupGateway courseGateway;
    private final NotesGeminiGateway notesGateway;
    private final GenerateLectureNotesOutputBoundary presenter;

    public GenerateLectureNotesInteractor(CourseLookupGateway courseGateway,
                                          NotesGeminiGateway notesGateway,
                                          GenerateLectureNotesOutputBoundary presenter) {
        this.courseGateway = courseGateway;
        this.notesGateway = notesGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(GenerateLectureNotesInputData inputData) {
        String courseId = inputData.getCourseId();
        String topic = inputData.getTopic();

        // 1. Look up the course (where the uploaded PDFs live)
        Course course = courseGateway.getCourseById(courseId);
        if (course == null) {
            presenter.prepareFailView("Course not found: " + courseId);
            return;
        }

        try {
            // 2. Ask Gemini (through the gateway) to generate notes
            LectureNotes lectureNotes = notesGateway.generateNotes(course, topic);

            // 3. Convert entity to output data for the presenter
            GenerateLectureNotesOutputData outputData =
                    new GenerateLectureNotesOutputData(
                            lectureNotes.getCourseId(),
                            lectureNotes.getTopic(),
                            lectureNotes.getContent(),
                            lectureNotes.getGeneratedAt()
                    );

            // 4. Tell the presenter it succeeded
            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            // 5. If anything goes wrong (Gemini failure, etc.), show an error
            presenter.prepareFailView("Failed to generate lecture notes. Please try again.");
        }
    }
}