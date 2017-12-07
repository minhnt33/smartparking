package hust.ict58.smartparking.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;

public class GetSlotStatusAction extends ActionInvocation {
    public GetSlotStatusAction(Service service) {
        super(service.getAction("GetStatus"));
    }
}
