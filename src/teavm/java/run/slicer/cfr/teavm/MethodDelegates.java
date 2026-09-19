package run.slicer.cfr.teavm;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MethodDelegates {
    private MethodDelegates() {
    }

    public static void java_util_logging_Logger_setUseParentHandlers(Logger logger, boolean useParentHandlers) {
        // no-op
    }

    public static void java_util_logging_Logger_addHandler(Logger logger, Handler handler) {
        // no-op
    }

    public static void java_util_logging_Logger_setLevel(Logger logger, Level level) {
        // no-op
    }
}
