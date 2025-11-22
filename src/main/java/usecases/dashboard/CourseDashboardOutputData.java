package usecases.dashboard;

import java.util.ArrayList;
import java.util.List;
import entities.*;


/**
 * Output data model for the course dashboard use case.
 * Contains the list of courses to be displayed on the dashboard.
 */
public class CourseDashboardOutputData {
    private List<Course> courses;

    /**
     * Constructs a CourseDashboardOutputData with the given list of courses.
     *
     * @param courses the list of courses to display
     */
    public CourseDashboardOutputData(List<Course> courses){
        this.courses = courses;
    }

    public List<Course> getCourses(){
        return this.courses;
    }
    public void setCourses(List<Course> courses){
        this.courses = courses;
    }
    /**
     * Returns the number of courses in the output data.
     *
     * @return the number of courses, or 0 if the courses list is null
     */
    public int size(){
        if (this.courses == null){
            return 0;
        }
        return this.courses.size();
    }

}
