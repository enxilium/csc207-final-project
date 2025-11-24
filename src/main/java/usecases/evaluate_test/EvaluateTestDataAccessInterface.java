package usecases.evaluate_test;

import entities.EvaluationData;
import entities.PDFFile;

import java.io.IOException;
import java.util.List;

public interface EvaluateTestDataAccessInterface {
    EvaluationData getEvaluationResults(List<PDFFile> courseMaterials, List<String> userAnswers, List<String> questions,
                                             List<String> answers) throws IOException;
}
