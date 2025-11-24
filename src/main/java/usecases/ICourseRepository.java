package usecases;
import entities.*;
import java.util.List;

public interface ICourseRepository {
    void create(Course course);
    void update(Course course);
    Course findById(String courseId);
    List<Course> findAll();
    void delete(String courseId);
}