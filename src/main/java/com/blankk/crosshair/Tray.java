package com.blankk.crosshair;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;

import static com.blankk.crosshair.NativeListener.addBindListeners;

public abstract class Tray {
    private static CrosshairController crosshairController;

    public static void createTrayIcon(Stage stage, CrosshairController controller) {
        crosshairController = controller;

        SystemTray tray = SystemTray.getSystemTray();
        Image icon = Toolkit.getDefaultToolkit().getImage(Tray.class.getResource("/com/blankk/crosshair/tray_icon.png"));
        TrayIcon trayIcon = new TrayIcon(icon, "Crosshair App");
        trayIcon.setImageAutoSize(true);

        PopupMenu popupMenu = new PopupMenu();

        MenuItem exitMenuItem = new MenuItem("Close");
        exitMenuItem.addActionListener((_) -> {
            tray.remove(trayIcon);

            Platform.runLater(stage::close);
        });

        popupMenu.add(exitMenuItem);

        Menu crosshairsMenu = new Menu("Crosshairs");

        MenuItem createCrosshairItem = new MenuItem("Add new crosshair");
        createCrosshairItem.addActionListener((_) -> Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Images", "*.png"));

                PauseTransition delay = new PauseTransition(Duration.seconds(0));

                delay.setOnFinished(_ -> {
                    File imageFile = fileChooser.showOpenDialog(stage);

                    if (imageFile != null) {
                        Crosshair newCrosshair =  crosshairController.addCrosshair(imageFile);

                        if (newCrosshair != null) {
                            addCrosshairToTray(newCrosshair, crosshairsMenu);

                            crosshairController.hideMessage();
                        } else {
                            crosshairController.showMessage("Image already exists!");

                            delay.setDuration(Duration.seconds(1));
                            delay.playFromStart();
                        }
                    } else {
                        crosshairController.hideMessage();
                    }
                });

                delay.play();
        }));

        crosshairsMenu.add(createCrosshairItem);

        crosshairController.getCrosshairs().forEach(c -> addCrosshairToTray(c, crosshairsMenu));

        popupMenu.add(crosshairsMenu);

        trayIcon.setPopupMenu(popupMenu);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void addCrosshairToTray(Crosshair crosshair, Menu crosshairsMenu) {
        Menu crosshairMenuItem = new Menu(crosshair.getName());

        crosshairsMenu.add(crosshairMenuItem);

        MenuItem deleteCrosshairItem = new MenuItem("Delete crosshair");
        deleteCrosshairItem.addActionListener((_) -> {
            crosshairController.removeCrosshair(crosshair);

            crosshairsMenu.remove(crosshairMenuItem);
        });

        crosshairMenuItem.add(deleteCrosshairItem);

        Menu bindsMenu = new Menu("Binds");

        MenuItem bindsItem = new MenuItem("Binds: ");
        bindsItem.setEnabled(false);
        bindsItem.setLabel("Binds: " + crosshair.getBinds().toString());

        MenuItem cleanBindsItem = new MenuItem("Clear");
        cleanBindsItem.addActionListener((_) -> {
            crosshair.cleanBinds();

            crosshairController.saveData();

            bindsItem.setLabel("Binds: ");
        });

        MenuItem addBindItem = new MenuItem("Add bind");
        addBindItem.addActionListener((_) -> {
            crosshairController.showMessage("Press any key or mouse button...");

            addBindListeners((bind, removeListeners) -> {
                if (!crosshairController.isBindUsed(bind)) {
                    crosshair.addBind(bind);

                    crosshairController.saveData();

                    crosshairController.hideMessage();

                    bindsItem.setLabel("Binds: " + crosshair.getBinds().toString());

                    removeListeners.run();
                } else {
                    crosshairController.showMessage("Bind already in use!");
                }
            });
        });

        bindsMenu.add(bindsItem);
        bindsMenu.add(cleanBindsItem);
        bindsMenu.add(addBindItem);

        crosshairMenuItem.add(bindsMenu);
    }
}
