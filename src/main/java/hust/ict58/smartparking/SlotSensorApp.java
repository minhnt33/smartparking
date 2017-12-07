package hust.ict58.smartparking;

import hust.ict58.smartparking.action.SetSlotStatusAction;
import hust.ict58.smartparking.device.Device;
import hust.ict58.smartparking.device.DeviceApp;
import hust.ict58.smartparking.service.SlotSensor;
import hust.ict58.smartparking.view.SlotSensorView;
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

public class SlotSensorApp extends DeviceApp {
    private final int SLOT_NUMBER = 5;
    private Device currentDevice;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Slot Sensor Control Panel");
        initializeDevices(SLOT_NUMBER, "Slot", "SlotSensor", "Using for detecting car", SlotSensor.class);
        initializeRootLayout();
    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        //System.out.println("Event: " + sub.getCurrentSequence().getValue());
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue status = values.get("Status");
        System.out.println("Status is: " + status.toString());
    }

    private void initializeRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SlotSensorView.fxml"));
            AnchorPane rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 320, 240);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Set app reference for controller
            SlotSensorView slotSensorViewController = loader.getController();
            slotSensorViewController.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = slotSensorDevices[index];
        Service service = getService(currentDevice.getDevice(), "SlotSensor");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }

    public void setSlotSensorState(String id, boolean state) {
        Service service = getService(currentDevice.getDevice(), "SlotSensor");

        if (service != null) {
            executeAction(upnpService, new SetSlotStatusAction(service, state));

            Action getStatusAction = service.getAction("GetStatus");
            ActionInvocation actionInvocation = new ActionInvocation(getStatusAction);
            ActionCallback getStatusCallback = new ActionCallback(actionInvocation) {
                @Override
                public void success(ActionInvocation invocation) {
                    ActionArgumentValue status = invocation.getOutput("ResultStatus");
                    System.out.println("Get status " + (boolean) status.getValue());
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