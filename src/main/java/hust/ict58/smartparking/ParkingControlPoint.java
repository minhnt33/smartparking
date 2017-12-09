package hust.ict58.smartparking;

import hust.ict58.smartparking.view.ControlPointView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParkingControlPoint extends Application {
    private ParkingGraph parkingGraph = new ParkingGraph();
    private HashMap<String, RemoteDevice> controlledDevices;
    private final UpnpService upnpService = new UpnpServiceImpl();
    private Stage primaryStage;
    private ControlPointView controlPointView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Smart Parking Control Panel");

        try {
            controlledDevices = new HashMap<>();

            // Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );

            // Display graph
            parkingGraph.display();
            parkingGraph.showNodeLabel();

            initializeRootLayout();
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

    private void initializeRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ControlPointView.fxml"));
            AnchorPane rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 320, 240);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Set app reference for controller
            controlPointView = loader.getController();
            controlPointView.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePropertyChangeCallback(UpnpService upnpService, Service service) {
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
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                StateVariableValue idVar = values.get("Id");

                // Only care about slot sensor and sign monitor data change
                if (idVar != null) {
                    String id = (String) idVar.getValue();

                    if (id.contains("Slot")) {
                        boolean status = (boolean) values.get("Status").getValue();
                        onSlotSensorDataChange(id, status);
                    } else if (id.contains("Sign")) {
                        StateVariableValue distanceVar = values.get("Distance");
                        StateVariableValue directionVar = values.get("Direction");

                        if (directionVar != null) {
                            String direction = (String) directionVar.getValue();

                            if (direction != null && direction.length() > 0)
                                onSignMonitorDirectionChange(id, direction);
                        }

                        if (distanceVar != null) {
                            double distance = (double) values.get("Distance").getValue();
                            onSignMonitorDistanceChange(id, distance);
                        }
                    }
                }
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

                if (deviceId.contains("Slot")) {
                    // Add device to hashmap
                    controlledDevices.put(deviceId, device);

                    // Set data change callback
                    Service slotSensorService = device.findService(new UDAServiceId("SlotSensor"));
                    if (slotSensorService != null) {
                        initializePropertyChangeCallback(upnpService, slotSensorService);
                    }
                }

                if (deviceId.contains("Sign")) {
                    // Add device to hashmap
                    controlledDevices.put(deviceId, device);

                    // Set data change callback
                    Service signMonitorService = device.findService(new UDAServiceId("SignMonitor"));
                    if (signMonitorService != null) {
                        initializePropertyChangeCallback(upnpService, signMonitorService);
                    }
                }

                System.out.println("Device discovered: " + deviceId);
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
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        System.err.println(defaultMsg);
                    }
                }
        );
    }

    private void onSignMonitorDirectionChange(String id, String direction) {
        parkingGraph.setNodeAttribute(id, "Direction", direction);
        double distance = parkingGraph.hasAttribute(id, "Distance") ? parkingGraph.getNodeDistanceAttribute(id) : 0.0f;
        parkingGraph.setNodeLabel(id, String.format("Direction: %s  Distance to next sign: %.1f", direction, distance));
    }

    private void onSignMonitorDistanceChange(String id, double distance) {
        parkingGraph.setNodeAttribute(id, "Distance", String.valueOf(distance));
        String direction = parkingGraph.hasAttribute(id, "Direction") ? parkingGraph.getNodeDirectionAttribute(id) : " ";
        parkingGraph.setNodeLabel(id, String.format("Direction: %s  Distance to next sign: %.1f", direction, distance));
    }

    private void onSlotSensorDataChange(String id, boolean status) {
        parkingGraph.setNodeColor(id, status ? "red" : "green");
        parkingGraph.setNodeAttribute(id, "Status", String.valueOf(status));
    }
}
