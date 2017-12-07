package hust.ict58.smartparking;

import hust.ict58.smartparking.action.SetDeviceIdAction;
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
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.HashMap;
import java.util.Map;

public class ParkingControlPoint implements Runnable {
    private ParkingGraph parkingGraph = new ParkingGraph();
    private HashMap<String, RemoteDevice> controlledDevices;

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread controlPointThread = new Thread(new ParkingControlPoint());
        controlPointThread.setDaemon(false);
        controlPointThread.start();
    }

    @Override
    public void run() {
        try {
            controlledDevices = new HashMap<>();
            UpnpService upnpService = new UpnpServiceImpl();

            // Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

    private void initializePropertyChangeCallback(UpnpService upnpService, Service service)
    {
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

                //System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                StateVariableValue status = values.get("Status");
                System.out.println("Status is: " + status.toString());

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

    private RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {
            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                String deviceId = device.getDetails().getFriendlyName();

                // Add device to hashmap
                controlledDevices.put(deviceId, device);
                System.out.println("Device discovered: " + deviceId);

                // Set device id to device service for easy identifying
                Service slotSensorService = device.findService(new UDAServiceId("SlotSensor"));

                if (slotSensorService != null) {
                    initializePropertyChangeCallback(upnpService, slotSensorService);
                    //executeAction(upnpService, new SetDeviceIdAction(slotSensorService, deviceId));
                }
            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                String deviceId = device.getDetails().getFriendlyName();

                // Remove device from hashmap
                controlledDevices.remove(deviceId);
                System.out.println("Device disappeared: " + deviceId);
            }
        };
    }

    private void executeAction(UpnpService upnpService, ActionInvocation action) {
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
}
