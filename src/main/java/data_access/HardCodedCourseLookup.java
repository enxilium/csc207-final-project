package data_access;

import entities.Course;
import entities.PDFFile;
import usecases.lecturenotes.CourseLookupGateway;

public class HardCodedCourseLookup implements CourseLookupGateway {
    private final Course demo;

    public HardCodedCourseLookup() {
        // Match the course you create in AppBuilder (PHL245)
        demo = new Course("PHL245", "Modern Symbolic Logic", "demo course");
        demo.addFile(new PDFFile("test.pdf")); // keep your local test.pdf
    }

    @Override
    public Course getCourseById(String courseId) {
        if (courseId == null) return null;
        return demo.getCourseId().equalsIgnoreCase(courseId) ? demo : null;
    }
}