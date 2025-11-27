package usecases.mock_test_generation;

public class MockTestGenerationInputData {
    private final String courseID;

    public MockTestGenerationInputData(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseID() {
        return courseID;
    }
}
