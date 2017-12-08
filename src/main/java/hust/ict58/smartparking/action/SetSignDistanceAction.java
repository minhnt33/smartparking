package hust.ict58.smartparking.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetSignDistanceAction extends ActionInvocation {
    public SetSignDistanceAction(Service service, double distance) {
        super(service.getAction("SetDistance"));

        try {
            setInput("NewDistance", distance);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
