package com.github.tommyettinger.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.tommyettinger.CaveCops;

import static com.github.tommyettinger.CaveCops.cellHeight;
import static com.github.tommyettinger.CaveCops.cellWidth;
import static com.github.tommyettinger.CaveCops.gridHeight;
import static com.github.tommyettinger.CaveCops.gridWidth;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        System.setProperty("org.lwjgl.librarypath", ".");
        return new Lwjgl3Application(new CaveCops(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("CaveCops");
//        configuration.useVsync(false);
        configuration.disableAudio(true);
        configuration.setForegroundFPS(300);
//        configuration.setForegroundFPS(0);
        configuration.setWindowedMode(gridWidth * cellWidth, gridHeight * cellHeight);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
