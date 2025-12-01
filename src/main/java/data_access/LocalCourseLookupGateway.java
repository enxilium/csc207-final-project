package data_access;

import entities.Course;
import usecases.lecturenotes.CourseLookupGateway;

public class LocalCourseLookupGateway implements CourseLookupGateway {
    private final LocalCourseRepository repo;

    public LocalCourseLookupGateway(LocalCourseRepository repo) {
        this.repo = repo;
    }

    @Override
    public Course getCourseById(String courseId) {
        return repo.findById(courseId);
    }
}
