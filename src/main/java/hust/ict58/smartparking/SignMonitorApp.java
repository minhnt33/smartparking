package hust.ict58.smartparking;

import hust.ict58.smartparking.action.SetSignDirectionAction;
import hust.ict58.smartparking.action.SetSignDistanceAction;
import hust.ict58.smartparking.device.Device;
import hust.ict58.smartparking.device.DeviceApp;
import hust.ict58.smartparking.service.SignMonitor;
import hust.ict58.smartparking.view.SignMonitorView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;

import java.io.IOException;
import java.util.Map;

public class SignMonitorApp extends DeviceApp {
    private final int SIGN_NUMBER = 3;
    private Device currentDevice;
    private SignMonitorView signMonitorViewController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Sign Control Panel");
        initializeDevices(SIGN_NUMBER, "Sign", "SignMonitor", "Using for displaying instructions", SignMonitor.class);
        initRootLayout();
        setServiceIds("SignMonitor");
        signMonitorViewController.populateSignList(devices);
    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue idVar = values.get("Id");

        if (idVar != null) {
            String id = (String) idVar.getValue();
            if (id.compareTo(currentDevice.getId()) == 0) {
                StateVariableValue directionVar = values.get("Direction");
                StateVariableValue distanceVar = values.get("Distance");

                if (directionVar != null) {
                    String direction = (String) directionVar.getValue();

                    if (direction != null && direction.length() > 0)
                        signMonitorViewController.setDirectionText(direction);
                    else
                        signMonitorViewController.setDirectionText(" ");
                }

                if (distanceVar != null) {
                    double distance = (double) values.get("Distance").getValue();
                    signMonitorViewController.setDistanceText(String.valueOf(distance));
                }
            }
        }
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SignMonitorView.fxml"));
            AnchorPane rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 320, 240);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Set app reference for controller
            signMonitorViewController = loader.getController();
            signMonitorViewController.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = devices[index];
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }

    public void setSignDirection(String direction) {
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            executeAction(upnpService, new SetSignDirectionAction(service, direction));
        }
    }

    public void setSignDistance(double distance) {
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            executeAction(upnpService, new SetSignDistanceAction(service, distance));
        }
    }

    public void getSignDirection() {
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            Action getStatusAction = service.getAction("GetDirection");
            ActionInvocation actionInvocation = new ActionInvocation(getStatusAction);
            ActionCallback getStatusCallback = new ActionCallback(actionInvocation) {
                @Override
                public void success(ActionInvocation invocation) {
                    ActionArgumentValue directionVar = invocation.getOutput("ResultDirection");

                    if (directionVar != null) {
                        String direction = (String) directionVar.getValue();

                        if (direction != null && direction.length() > 0)
                            signMonitorViewController.setDirectionText(direction);
                        else
                            signMonitorViewController.setDirectionText(" ");
                    }
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    System.err.println(defaultMsg);
                }
            };
            upnpService.getControlPoint().execute(getStatusCallback);
        }
    }

    public void getSignDistance() {
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            Action getStatusAction = service.getAction("GetDistance");
            ActionInvocation actionInvocation = new ActionInvocation(getStatusAction);
            ActionCallback getStatusCallback = new ActionCallback(actionInvocation) {
                @Override
                public void success(ActionInvocation invocation) {
                    ActionArgumentValue distance = invocation.getOutput("ResultDistance");
                    signMonitorViewController.setDistanceText((String) distance.getValue());
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    System.err.println(defaultMsg);
                }
            };
            upnpService.getControlPoint().execute(getStatusCallback);
        }
    }
}
