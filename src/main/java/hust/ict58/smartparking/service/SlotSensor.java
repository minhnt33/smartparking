package hust.ict58.smartparking.service;

import org.fourthline.cling.binding.annotations.*;

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

    @UpnpStateVariable()
    private String id = "Unknown";

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setId(@UpnpInputArgument(name = "NewIdValue") String newId) {
        id = newId;
    }

    @UpnpAction
    public void setStatus(@UpnpInputArgument(name = "NewStatusValue") boolean newStatusValue) {
        getPropertyChangeSupport().firePropertyChange("Status", status, newStatusValue);
        status = newStatusValue;
        System.out.printf("%s change status to %s\n", id, status);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return status;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultId"))
    public String getId() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return id;
    }
}
