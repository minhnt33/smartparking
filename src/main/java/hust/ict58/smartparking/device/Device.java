package hust.ict58.smartparking.device;

import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;

/**
 * Base class for all devices
 */
public class Device {
    private LocalDevice device;
    private String id;
    private String type;
    private int version;
    private String friendlyName;
    private String manufacturer;
    private String modelName;
    private String description;
    private String modelNumber;
    private Class service;

    public Device(String id, String type, int version, String friendlyName, String manufacturer, String modelName, String description, String modelNumber, Class service) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.friendlyName = friendlyName;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.description = description;
        this.modelNumber = modelNumber;
        this.service = service;
    }

    public Device() {

    }

    public void initializeDevice() {
        try {
            device = createLocalDevice(id, type, version, friendlyName, manufacturer, modelName, description, modelNumber, service);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private LocalDevice createLocalDevice(String id, String typeName, int version, String friendlyName, String manufacturer, String modelName, String description, String modelNumber, Class service) throws ValidationException, LocalServiceBindingException, IOException {
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(id));
        DeviceType type = new UDADeviceType(typeName, version);
        DeviceDetails details = new DeviceDetails(friendlyName, new ManufacturerDetails(manufacturer), new ModelDetails(modelName, description, modelNumber));
        Icon icon = new Icon("image/png", 48, 48, 8, getClass().getResource("/icon.png"));

        LocalService deviceService = new AnnotationLocalServiceBinder().read(service);
        deviceService.setManager(new DefaultServiceManager(deviceService, service));

        return new LocalDevice(identity, type, details, icon, deviceService);
    }

    public LocalDevice getDevice() {
        return device;
    }

    public String getId() {
        return id;
    }
}
