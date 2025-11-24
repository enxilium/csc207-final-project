package usecases.mock_test_generation;

public interface MockTestGenerationOutputBoundary {
    void presentTest(MockTestGenerationOutputData mockTestGenerationOutputData);

    void presentLoading();

    void presentError(String errorMessage);
}
