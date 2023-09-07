package me.tecnio.ahm.alert.alert;

import me.tecnio.ahm.alert.alert.impl.ThreadedAlertHandler;
import me.tecnio.ahm.util.Factory;

public class AlertHandlerFactory implements Factory<AlertHandler> {

    @Override
    public AlertHandler build() {
        return new ThreadedAlertHandler();
    }
}
