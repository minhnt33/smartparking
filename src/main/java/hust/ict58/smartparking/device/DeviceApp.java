package hust.ict58.smartparking.device;

import hust.ict58.smartparking.action.SetDeviceIdAction;
import javafx.application.Application;
import javafx.stage.Stage;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;

public abstract class DeviceApp extends Application {
    protected Device[] devices;
    protected UpnpService upnpService;

    // JavaFX
    protected Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        upnpService = new UpnpServiceImpl();
        this.primaryStage = primaryStage;
    }

    protected void executeAction(UpnpService upnpService, ActionInvocation action) {
        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(
                new ActionCallback(action) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
                        System.out.println("Successfully called action " + invocation.getClass().getSimpleName());
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        System.err.println(defaultMsg);
                    }
                }
        );
    }

    /**
     * Create upnp devices
     */
    private void createDevices(int amount, String prefix, String type, String description, Class deviceClass) {
        // Create slot devices
        devices = new Device[amount];
        for (int i = 0; i < amount; ++i) {
            // Create device model
            String id = prefix.concat(String.valueOf(i));
            Device device = new Device(id, type, 1, id, "ICT58", "2k17", description, "v1", deviceClass);
            device.initializeDevice();
            devices[i] = device;
        }
    }

    protected void initializeDevices(int amount, String prefix, String type, String description, Class deviceClass) {
        createDevices(amount, prefix, type, description, deviceClass);

        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            addDevices(devices);
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Add devices into upnp services
     *
     * @param devices
     */
    private void addDevices(Device[] devices) {
        for (Device device : devices) {
            upnpService.getRegistry().addDevice(device.getDevice());
        }
    }

    protected Service getService(LocalDevice device, String serviceId) {
        return device.findService(new UDAServiceId(serviceId));
    }

    protected void setServiceIds(String serviceIds)
    {
        for(Device device : devices) {
            Service service = getService(device.getDevice(), serviceIds);

            if(service != null) {
                executeAction(upnpService, new SetDeviceIdAction(service, device.getId()));
            }
        }
    }

    protected void initializePropertyChangeCallback(UpnpService upnpService, Service service) {
        SubscriptionCallback callback = new SubscriptionCallback(service, 600) {

            @Override
            public void established(GENASubscription sub) {
                //System.out.println("Established: " + sub.getSubscriptionId());
            }

            @Override
            protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
                System.err.println(defaultMsg);
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
            }

            @Override
            public void eventReceived(GENASubscription sub) {
                onPropertyChangeCallbackReceived(sub);
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
            }

            @Override
            protected void invalidMessage(RemoteGENASubscription sub, UnsupportedDataException ex) {
                // Log/send an error report?
            }
        };

        upnpService.getControlPoint().execute(callback);
    }

    public abstract void onPropertyChangeCallbackReceived(GENASubscription subscription);
}
