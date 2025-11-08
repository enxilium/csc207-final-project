package data_access;

import entities.Course;
import entities.PDFFile;
import usecases.lecturenotes.CourseLookupGateway;

/**
 * TEMPORARY hard-coded implementation of CourseLookupGateway
 * so you can test lecture-notes generation before the real
 * Course Management logic is ready.
 */
public class HardCodedCourseLookup implements CourseLookupGateway {

    private final Course demoCourse;

    public HardCodedCourseLookup() {
        // Pretend we have a single course "CSC207"
        this.demoCourse = new Course("CSC207");

        // This path should point to a real PDF file on your machine
        // For now, we use "test.pdf" in the project root (you already have one).
        this.demoCourse.addFile(new PDFFile("test.pdf"));
    }

    @Override
    public Course getCourseById(String courseId) {
        if (demoCourse.getCourseId().equals(courseId)) {
            return demoCourse;
        }
        return null;
    }
}