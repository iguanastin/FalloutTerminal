package fo.terminal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.graphics.Color;
import fo.terminal.TerminalMain;

import java.awt.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.backgroundFPS = 0;
        config.foregroundFPS = 60;
        config.vSyncEnabled = true;
        config.fullscreen = false;
        config.initialBackgroundColor = Color.BLACK;
        config.resizable = true;
        config.title = "Fallout Terminal";
        config.x = -1;
        config.y = -1;
        config.width = width / 2;
        config.height = height / 2;
        new LwjglFrame(new TerminalMain(), config);
    }
}
