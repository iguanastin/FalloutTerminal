package fot.terminal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.graphics.Color;
import fot.terminal.TerminalMain;

public class DesktopLauncher {
    public static void main(String[] arg) {
//        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.backgroundFPS = 0;
//        config.foregroundFPS = LwjglApplicationConfiguration.getDesktopDisplayMode().refreshRate;
        config.foregroundFPS = 60;
        config.fullscreen = false;
        config.initialBackgroundColor = Color.BLACK;
        config.resizable = true;
        config.title = "Fallout Terminal";
        config.x = -1;
        config.y = -1;
        config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
        config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;

        TerminalMain terminalMain = new TerminalMain();
        LwjglFrame frame = new LwjglFrame(terminalMain, config);
        frame.setExtendedState(LwjglFrame.MAXIMIZED_BOTH);
        terminalMain.setLwjglFrame(frame);

    }
}
