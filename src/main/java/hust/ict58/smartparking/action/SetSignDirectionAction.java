package hust.ict58.smartparking.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetSignDirectionAction extends ActionInvocation {
    public SetSignDirectionAction(Service service, String direction) {
        super(service.getAction("SetDirection"));

        try {
            setInput("NewDirection", direction);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
