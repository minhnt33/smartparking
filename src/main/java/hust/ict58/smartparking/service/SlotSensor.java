package hust.ict58.smartparking.service;

import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.ModelUtil;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("SlotSensor"),
        serviceType = @UpnpServiceType(value = "SlotSensor", version = 1)
)

public class SlotSensor {
    private final PropertyChangeSupport propertyChangeSupport;

    public SlotSensor() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "Unknown")
    private String id;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false; // Note that status indicates if there was a car at this slot. So true mean slot is unavailable, and vice versa

    @UpnpAction
    public void setId(@UpnpInputArgument(name = "NewId") String newId) {
        id = newId;
    }

    @UpnpAction
    public void setStatus(@UpnpInputArgument(name = "NewStatus") boolean newStatus) {
        status = newStatus;
        getPropertyChangeSupport().firePropertyChange("Status", null, null);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return status;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultId"))
    public String getId() {
        return id;
    }
}
