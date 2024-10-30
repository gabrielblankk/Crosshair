package com.blankk.crosshair;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Crosshair implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;

    private final String image;

    private final Set<String> binds = new HashSet<>();

    public Crosshair(File image) {
        this.name = image.getName().replace(".png", "");
        this.image = image.getName();
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void addBind(String bind) {
        binds.add(bind);
    }

    public void cleanBinds() {
        binds.clear();
    }

    public Set<String> getBinds() {
        return binds;
    }
}
