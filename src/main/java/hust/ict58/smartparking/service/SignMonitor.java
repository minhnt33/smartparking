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

    @UpnpStateVariable(defaultValue = "Unknown")
    private String id;

    @UpnpStateVariable(defaultValue = "forward")
    private String direction;

    @UpnpStateVariable(defaultValue = "0")
    private double distance;

    @UpnpAction
    public void setId(@UpnpInputArgument(name = "NewId") String newId) {
        id = newId;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultId"))
    public String getId() {
        return id;
    }

    @UpnpAction
    public void setDirection(@UpnpInputArgument(name = "NewDirection") String newDirection) {
        direction = newDirection;
        getPropertyChangeSupport().firePropertyChange("Direction", null, null);

    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDirection"))
    public String getDirection() {
        return direction;
    }

    @UpnpAction
    public void setDistance(@UpnpInputArgument(name = "NewDistance") double newDistance) {
        distance = newDistance;
        getPropertyChangeSupport().firePropertyChange("Distance", null, null);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDistance"))
    public double getDistance() {
        return distance;
    }
}