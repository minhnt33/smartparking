package hust.ict58.smartparking.view;

import hust.ict58.smartparking.ParkingControlPoint;
import javafx.fxml.FXML;

public class ControlPointView {
    private ParkingControlPoint app;

    @FXML
    private void onHasCar() {

    }

    public void setApp(ParkingControlPoint app) {
        this.app = app;
    }
}
