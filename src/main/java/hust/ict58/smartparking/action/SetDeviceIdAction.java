package hust.ict58.smartparking.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetDeviceIdAction extends ActionInvocation {
    public SetDeviceIdAction(Service service, String id) {
        super(service.getAction("SetId"));

        try {
            // Throws InvalidValueException if the value is of wrong type
            setInput("NewIdValue", id);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}

