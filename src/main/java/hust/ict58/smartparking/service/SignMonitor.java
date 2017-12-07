package hust.ict58.smartparking.service;

import org.fourthline.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("SignMonitor"),
        serviceType = @UpnpServiceType(value = "SignMonitor", version = 1)
)

public class SignMonitor {
    @UpnpStateVariable(defaultValue = "forward")
    private String direction;

    @UpnpStateVariable(defaultValue = "0")
    private float distanceToNextNode;

    @UpnpAction
    public void setDirection(@UpnpInputArgument(name = "newInstruction") String newInstruction) {
        direction = newInstruction;
        System.out.println("New Instruction: " + direction);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDirection"))
    public String getDirection() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return direction;
    }

    @UpnpAction
    public void setDistance(@UpnpInputArgument(name = "newDistance") float newDistance) {
        distanceToNextNode = newDistance;
        System.out.println("Distance to next node: " + distanceToNextNode);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDistance"))
    public float getDistanceToNextNode() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return distanceToNextNode;
    }
}