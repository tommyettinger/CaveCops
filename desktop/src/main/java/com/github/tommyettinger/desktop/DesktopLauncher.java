package com.github.tommyettinger.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.tommyettinger.CaveCops;

import static com.github.tommyettinger.CaveCops.*;

/** Launches the desktop (LWJGL 2) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {
        return new LwjglApplication(new CaveCops(), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = ("CaveCops");
        configuration.vSyncEnabled = true;
        configuration.foregroundFPS = 0;
        configuration.width = gridWidth * cellWidth;
        configuration.height = gridHeight * cellHeight;
        configuration.forceExit = false;
        configuration.addIcon("libgdx128.png", Files.FileType.Internal);
        configuration.addIcon("libgdx64.png", Files.FileType.Internal);
        configuration.addIcon("libgdx32.png", Files.FileType.Internal);
        configuration.addIcon("libgdx16.png", Files.FileType.Internal);
        return configuration;
    }
}
