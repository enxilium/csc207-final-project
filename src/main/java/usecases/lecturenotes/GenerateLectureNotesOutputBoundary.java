package usecases.lecturenotes;

public interface GenerateLectureNotesOutputBoundary {
    void prepareSuccessView(GenerateLectureNotesOutputData outputData);
    void prepareFailView(String error);
}