package usecases.evaluate_test;

public interface EvaluateTestOutputBoundary {
    void presentEvaluationResults(EvaluateTestOutputData evaluateTestOutputData);

    void presentLoading();

    void presentError(String errorMessage);
}
