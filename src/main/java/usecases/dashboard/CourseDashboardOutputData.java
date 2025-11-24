package usecases.dashboard;

import java.util.ArrayList;
import java.util.List;
import entities.*;


public class CourseDashboardOutputData {
    private List<Course> courses;

    public CourseDashboardOutputData(List<Course> courses){
        this.courses = courses;
    }

    public List<Course> getCourses(){
        return this.courses;
    }
    public void setCourses(List<Course> courses){
        this.courses = courses;
    }
    public int size(){
        if (this.courses == null){
            return 0;
        }
        return this.courses.size();
    }

}
