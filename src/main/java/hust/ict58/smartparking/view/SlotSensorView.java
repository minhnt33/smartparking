package hust.ict58.smartparking.view;

import hust.ict58.smartparking.SlotSensorApp;
import hust.ict58.smartparking.device.Device;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class SlotSensorView {
    @FXML
    private RadioButton availableBut;
    @FXML
    private RadioButton unavailableBut;
    @FXML
    private ComboBox comboBox;

    private ObservableList<String> slotSensorDeviceIds = FXCollections.observableArrayList();
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private boolean status;
    private SlotSensorApp app;

    private static final String STATE_AVAILABLE = "available";
    private static final String STATE_UNAVAILABLE = "unavailable";

    @FXML
    private void initialize() {
        // ComboBox
        comboBox.setItems(slotSensorDeviceIds);

        // Radio Button
        availableBut.setUserData(STATE_AVAILABLE);
        unavailableBut.setUserData(STATE_UNAVAILABLE);
        availableBut.setToggleGroup(toggleGroup);
        unavailableBut.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                // Parse status value
                String statusStr = toggleGroup.getSelectedToggle().getUserData().toString();
                if (statusStr.compareTo(STATE_AVAILABLE) == 0) {
                    status = false;
                } else if (statusStr.compareTo(STATE_UNAVAILABLE) == 0) {
                    status = true;
                }

                app.setSlotSensorState(status);
            }
        });
    }

    @FXML
    private void onSelectSlotSensor() {
        String selectedStr = comboBox.getValue().toString();
        selectedStr = selectedStr.replace("Slot", "");
        int selectedIndex = Integer.valueOf(selectedStr);
        app.setCurrentDevice(selectedIndex);
        System.out.println("Select sensor " + selectedIndex);
    }

    public void populateSlotSensorList(Device[] devices)
    {
        for (Device device : devices)
        {
            slotSensorDeviceIds.add(device.getId());
        }
    }

    public void updateSlotStatusUI(boolean status)
    {
        if(status)
        {
            unavailableBut.fire();
        }
        else
        {
            availableBut.fire();
        }
    }

    public void setApp(SlotSensorApp app) {
        this.app = app;
    }
}
