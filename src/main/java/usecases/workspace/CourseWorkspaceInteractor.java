package usecases.workspace;

import java.util.List;
import java.util.Optional;
import entities.*;
import interface_adapters.dashboard.*;
import usecases.ICourseRepository;
import usecases.dashboard.*;



public class CourseWorkspaceInteractor implements CourseWorkspaceInputBoundary {
    private final ICourseRepository courseRepository;
    private final CourseWorkspaceOutputBoundary courseWorkspacePresenter;
    private final CourseDashboardOutputBoundary courseDashboardPresenter;

    public CourseWorkspaceInteractor(ICourseRepository courseRepository, CourseWorkspaceOutputBoundary courseWorkspacePresenter, CourseDashboardOutputBoundary courseDashboardPresenter){
        this.courseRepository = courseRepository;
        this.courseWorkspacePresenter = courseWorkspacePresenter;
        this.courseDashboardPresenter = courseDashboardPresenter;
    }


    @Override
    public void findCourseById(String courseId, boolean isEdit){
        Course course = this.courseRepository.findById(courseId);
        if (course == null ){
            this.courseWorkspacePresenter.prepareFailView("There is no course");
        }else{
            CourseWorkspaceOutputData courseWorkspaceOutputData = new CourseWorkspaceOutputData(course);
            if (isEdit){
                this.courseWorkspacePresenter.prepareEditView(courseWorkspaceOutputData);
            }else{
                this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
            }
        }
    }

    @Override
    public void createCourse(entities.Course course){
        if (course == null){
            throw new IllegalArgumentException("course is null");
        }
        if (course.getCourseId() == null){
            throw new IllegalArgumentException("course id is null");
        }

        List<entities.Course> courses = this.courseRepository.findAll();
        Optional<entities.Course> foundObject = courses.stream()
                .filter(obj -> obj.getCourseId().equals(course.getCourseId()))
                .findFirst();
        if (foundObject.isPresent()){
            throw new RuntimeException("course already exist, course id: " + course.getCourseId());
        }
        this.courseRepository.create(course);
        CourseWorkspaceOutputData courseWorkspaceOutputData = new CourseWorkspaceOutputData(course);
        this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
    }


    @Override
    public void updateCourse(entities.Course course){
        if (course == null){
            throw new IllegalArgumentException("course is null");
        }
        if (course.getCourseId() == null){
            throw new IllegalArgumentException("course id is null");
        }

        List<entities.Course> courses = this.courseRepository.findAll();
        Optional<entities.Course> foundObject = courses.stream()
                .filter(obj -> obj.getCourseId().equals(course.getCourseId()))
                .findFirst();
        if (!foundObject.isPresent()){
            throw new RuntimeException("course does not exist, course id: " + course.getCourseId());
        }
        this.courseRepository.update(course);
        CourseWorkspaceOutputData courseWorkspaceOutputData = new CourseWorkspaceOutputData(course);
        this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
    }

    //deleteCourse
    @Override
    public void deleteCourse(String courseId){
        if (courseId == null){
            throw new IllegalArgumentException("course id is null");
        }

        this.courseRepository.delete(courseId);
        List<Course> courses = this.courseRepository.findAll();

        CourseDashboardOutputData courseDashboardOutputData = new CourseDashboardOutputData(courses);
        this.courseDashboardPresenter.prepareDashboardView(courseDashboardOutputData);
    }
}

