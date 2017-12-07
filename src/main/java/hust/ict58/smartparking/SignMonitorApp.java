package hust.ict58.smartparking;

import hust.ict58.smartparking.device.DeviceApp;
import org.fourthline.cling.model.gena.GENASubscription;

public class SignMonitorApp extends DeviceApp {
    private final int SIGN_NUMBER = 3;

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {

    }
}
