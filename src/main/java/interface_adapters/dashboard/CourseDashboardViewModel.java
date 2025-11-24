package interface_adapters.dashboard;
import interface_adapters.*;


public class CourseDashboardViewModel extends ViewModel<CourseDashboardState>{
    public CourseDashboardViewModel() {
        super("dashboard");
        setState(new CourseDashboardState());
    }
}
