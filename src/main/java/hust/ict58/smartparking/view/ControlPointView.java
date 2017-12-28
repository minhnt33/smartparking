package hust.ict58.smartparking.view;

import hust.ict58.smartparking.ParkingControlPoint;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ControlPointView {
    @FXML
    private TextField textField;

    private ParkingControlPoint app;

    @FXML
    private void onHasCar() {
        app.findPathToNearestAvailableSlot();
    }

    @FXML
    private void onLoadGraph()
    {
        if(textField.getText() != null && textField.getText().length() > 0) {
            app.loadGraph(textField.getText());
        }
    }

    public void setApp(ParkingControlPoint app) {
        this.app = app;
    }
}
