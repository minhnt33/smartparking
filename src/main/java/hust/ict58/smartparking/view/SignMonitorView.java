package hust.ict58.smartparking.view;

import hust.ict58.smartparking.SignMonitorApp;
import hust.ict58.smartparking.device.Device;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SignMonitorView {
    @FXML
    private TextField directionText;

    @FXML
    private TextField distanceText;

    @FXML
    private ComboBox comboBox;

    private ObservableList<String> signDeviceIds = FXCollections.observableArrayList();
    private SignMonitorApp app;

    @FXML
    private void initialize() {
        comboBox.setItems(signDeviceIds);
    }

    public void setDirectionText(String directionText) {
        this.directionText.setText(directionText);
    }

    public void setDistanceText(String distanceText) {
        this.distanceText.setText(distanceText);
    }

    public void setApp(SignMonitorApp app) {
        this.app = app;
    }

    public void populateSignList(Device[] devices) {
        for (Device device : devices) {
            signDeviceIds.add(device.getId());
        }
    }

    @FXML
    private void onSelectSign() {
        String selectedStr = comboBox.getValue().toString();
        selectedStr = selectedStr.replace("Sign", "");
        int selectedIndex = Integer.valueOf(selectedStr);
        System.out.println("Select sign " + selectedIndex);
        app.setCurrentDevice(selectedIndex);
    }

    @FXML
    private void onUpdateField() {
        if (directionText.getText() != null && distanceText.getText() != null) {
            app.setSignDirection(directionText.getText());
            app.setSignDistance(Double.valueOf(distanceText.getText()));
        }
    }

    public double getCurrentDistance() {
        return Double.valueOf(distanceText.getText());
    }

    public String getCurrentDirection() {
        return directionText.getText();
    }
}
