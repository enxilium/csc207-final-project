package interface_adapters.workspace;
import interface_adapters.*;


public class CourseCreateViewModel extends ViewModel<CourseState>{
    public CourseCreateViewModel() {
        super("createCourse");
        setState(new CourseState());
    }
}
