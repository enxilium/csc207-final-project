package usecases.dashboard;

import java.util.List;
import java.util.Optional;
import usecases.*;


public class CourseDashboardInteractor implements  CourseDashboardInputBoundary {
    private final ICourseRepository courseRepository;
    private final CourseDashboardOutputBoundary coursePresenter;

    public CourseDashboardInteractor(ICourseRepository courseRepository, CourseDashboardOutputBoundary coursePresenter){
        this.courseRepository = courseRepository;
        this.coursePresenter = coursePresenter;
    }

    @Override
    public void getCourses(){
        List<entities.Course> courses = this.courseRepository.findAll();
        if (courses == null || courses.isEmpty()){
            this.coursePresenter.prepareFailView("There is no course");
        }else{
            CourseDashboardOutputData courseDashboardOutputData = new CourseDashboardOutputData(courses);
            this.coursePresenter.prepareDashboardView(courseDashboardOutputData);
        }
    }


    @Override
    public void createCourse(){
        this.coursePresenter.prepareCreateCourseView();
    }
}

