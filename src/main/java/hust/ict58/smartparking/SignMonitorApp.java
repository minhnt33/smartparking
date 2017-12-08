package hust.ict58.smartparking;

import hust.ict58.smartparking.device.Device;
import hust.ict58.smartparking.device.DeviceApp;
import hust.ict58.smartparking.service.SignMonitor;
import hust.ict58.smartparking.view.SignMonitorView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;

import java.io.IOException;
import java.util.Map;

public class SignMonitorApp extends DeviceApp {
    private final int SIGN_NUMBER = 3;
    private Device currentDevice;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Sign Control Panel");
        initializeDevices(SIGN_NUMBER, "Sign", "SignMonitor", "Using for displaying instructions", SignMonitor.class);
        initRootLayout();
    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue status = values.get("Direction");
        System.out.println("Direction is: " + status.toString());
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
            SignMonitorView signMonitorViewController = loader.getController();
            signMonitorViewController.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = slotSensorDevices[index];
        Service service = getService(currentDevice.getDevice(), "SignMonitor");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }
}
