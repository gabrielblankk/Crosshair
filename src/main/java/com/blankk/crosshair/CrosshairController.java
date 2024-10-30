package com.blankk.crosshair;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class CrosshairController {
    private final File crosshairDir = new File(System.getProperty("user.home"),"crosshair");

    private final File imagesDir = new File(crosshairDir,"images");

    private final Set<Crosshair> crosshairs = new HashSet<>();

    private final String dataFile = "crosshairs.dat";

    @FXML
    private ImageView crosshairImageView;

    @FXML
    private Text bindLabel;

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        if (!crosshairDir.exists()) {
            crosshairDir.mkdir();
            if (!imagesDir.exists()) {
                imagesDir.mkdir();
            }
        }

        File crosshairFile = new File(crosshairDir, dataFile);

        if (crosshairFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(crosshairFile))) {
                Set<Crosshair> loadedCrosshairs = (Set<Crosshair>) ois.readObject();

                crosshairs.clear();
                crosshairs.addAll(loadedCrosshairs);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(crosshairDir, dataFile)));

            oos.writeObject(crosshairs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleKeyPress(String key) {
        crosshairs.forEach(c -> {
            if (c.getBinds().contains(key)) {
                loadCrosshair(c.getImage());
            }
        });
    }

    public Crosshair addCrosshair(File image) {
        Crosshair newCrosshair = new Crosshair(image);

        if (saveImage(image)) {
            loadCrosshair(image.getName());

            crosshairs.add(newCrosshair);

            saveData();

            return newCrosshair;
        }
        return null;
    }

    public void removeCrosshair(Crosshair crosshair) {
        File crosshairFile = new File(imagesDir, crosshair.getImage());

        if (crosshairFile.exists()) {
            crosshairFile.delete();
        }

        crosshairs.remove(crosshair);

        saveData();

        loadCrosshair(null);
    }

    public Set<Crosshair> getCrosshairs() {
        return crosshairs;
    }

    private void loadCrosshair(String image) {
        if (image == null) {
            crosshairImageView.setImage(null);
        } else {
            File crosshairFile = new File(imagesDir, image);

            if (crosshairFile.exists()) {
                crosshairImageView.setImage(new Image(crosshairFile.toURI().toString()));
            }
        }
    }

    private boolean saveImage(File image) {
        if (imagesDir.exists()) {
            Path destination = Paths.get(imagesDir.getAbsolutePath(), image.getName());

            if (Files.exists(destination)) {
                return false;
            } else {
                try {
                    Files.copy(image.toPath(), destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean isBindUsed(String bind) {
        for (Crosshair c : crosshairs) {
            if (c.getBinds().contains(bind)) {
                return true;
            }
        }

        return false;
    }

    public void showMessage(String message) {
        bindLabel.setText(message);
    }

    public void hideMessage() {
        bindLabel.setText("");
    }
}