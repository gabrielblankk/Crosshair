module com.blankk.crosshair {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.github.kwhat.jnativehook;
    requires java.prefs;

    opens com.blankk.crosshair to javafx.fxml;
    exports com.blankk.crosshair;
}