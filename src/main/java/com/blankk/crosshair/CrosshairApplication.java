package com.blankk.crosshair;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.blankk.crosshair.NativeListener.addBindListeners;


public class CrosshairApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, NativeHookException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("crosshair-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        CrosshairController crosshairController = fxmlLoader.getController();

        scene.setFill(null);

        stage.setScene(scene);

        stage.setTitle("Crosshair");

        stage.setAlwaysOnTop(true);

        stage.initStyle(StageStyle.TRANSPARENT);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);

        double screenX = (screenWidth - stage.getWidth()) / 2;
        double screenY = (screenHeight - stage.getHeight()) / 2;
        stage.setX(screenX);
        stage.setY(screenY);

        stage.show();

        HWND hwnd = User32.INSTANCE.FindWindow(null, stage.getTitle());

        final int WS_EX_LAYERED = 0x00080000;
        final int WS_EX_TRANSPARENT = 0x00000020;
        final int WS_EX_TOOLWINDOW = 0x00000080;

        int windowStyle = User32.INSTANCE.GetWindowLong(hwnd, User32.GWL_EXSTYLE);
        windowStyle = windowStyle | WS_EX_LAYERED | WS_EX_TRANSPARENT | WS_EX_TOOLWINDOW;
        User32.INSTANCE.SetWindowLong(hwnd, User32.GWL_EXSTYLE, windowStyle);


        Tray.createTrayIcon(stage, crosshairController);


        String tempDir = System.getProperty("java.io.tmpdir");
        System.setProperty("jnativehook.lib.path", tempDir);

        GlobalScreen.registerNativeHook();

        addBindListeners((bind, _) -> crosshairController.handleKeyPress(bind));
    }

    @Override
    public void stop() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
    }

    public static void main(String[] args) { launch(); }
}