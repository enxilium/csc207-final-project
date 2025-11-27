package usecases.evaluate_test;

import entities.PDFFile;

import java.util.List;

public interface EvaluateTestCourseDataAccessInterface {
    List<PDFFile> getCourseMaterials(String courseId);
}
