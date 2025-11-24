package interface_adapters.evaluate_test;

import interface_adapters.ViewModel;

public class EvaluateTestViewModel extends ViewModel<EvaluateTestState> {
    public EvaluateTestViewModel() {
        super("evaluate test");
        setState(new EvaluateTestState());
    }
}
