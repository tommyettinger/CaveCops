package com.github.tommyettinger.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.tommyettinger.CaveCops;

import static com.github.tommyettinger.CaveCops.cellHeight;
import static com.github.tommyettinger.CaveCops.cellWidth;
import static com.github.tommyettinger.CaveCops.gridHeight;
import static com.github.tommyettinger.CaveCops.gridWidth;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(gridWidth * cellWidth, gridHeight * cellHeight);
        return configuration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new CaveCops();
    }
}