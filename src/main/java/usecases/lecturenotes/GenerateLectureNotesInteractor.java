package usecases.lecturenotes;


import entities.Course;
import entities.LectureNotes;

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

        Course course = courseGateway.getCourseById(courseId);

        // If course not found, fail early
        if (course == null) {
            presenter.prepareFailView("Course not found: " + courseId);
            return;
        }

        try {
            LectureNotes lectureNotes = notesGateway.generateNotes(course, topic);

            GenerateLectureNotesOutputData outputData = new GenerateLectureNotesOutputData(
                    lectureNotes.getCourseId(),
                    lectureNotes.getTopic(),
                    lectureNotes.getContent()
            );
            presenter.prepareSuccessView(outputData);

        } catch (NotesGenerationException e) {
            e.printStackTrace();
            presenter.prepareFailView("Failed to generate lecture notes. Please try again.");
        }
    }
}