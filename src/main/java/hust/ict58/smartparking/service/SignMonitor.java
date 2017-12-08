package hust.ict58.smartparking.service;

import org.fourthline.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("SignMonitor"),
        serviceType = @UpnpServiceType(value = "SignMonitor", version = 1)
)

public class SignMonitor {
    private final PropertyChangeSupport propertyChangeSupport;

    public SignMonitor() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "forward")
    private String direction;

    @UpnpStateVariable(defaultValue = "0")
    private float distance;

    @UpnpAction
    public void setDirection(@UpnpInputArgument(name = "newDirection") String newDirection) {
        getPropertyChangeSupport().firePropertyChange("Direction", direction, newDirection);
        direction = newDirection;
        System.out.println("New Direction: " + direction);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDirection"))
    public String getDirection() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return direction;
    }

    @UpnpAction
    public void setDistance(@UpnpInputArgument(name = "newDistance") float newDistance) {
        getPropertyChangeSupport().firePropertyChange("Distance", distance, newDistance);
        distance = newDistance;
        System.out.println("Distance to next node: " + distance);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDistance"))
    public float getDistance() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return distance;
    }
}