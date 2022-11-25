package cum.jesus.jesusclient.utils;

import jline.internal.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
    public static String getColouredBoolean(boolean bool) {
        return bool ? (EnumChatFormatting.GREEN + "Enabled") : (EnumChatFormatting.RED + "Disabled");
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Point calculateMouseLocation() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int scale = minecraft.gameSettings.guiScale;
        if (scale == 0)
            scale = 1000;
        int scaleFactor = 0;
        while (scaleFactor < scale && minecraft.displayWidth / (scaleFactor + 1) >= 320 && minecraft.displayHeight / (scaleFactor + 1) >= 240)
            scaleFactor++;
        return new Point(Mouse.getX() / scaleFactor, minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }

    public static final void playSound(@NotNull File file, int volume) {
        if (file.exists())
            try {
                AudioInputStream audio = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                if (clip.getControl(FloatControl.Type.MASTER_GAIN) == null) {
                    clip.getControl(FloatControl.Type.MASTER_GAIN);
                    throw new NullPointerException("null cannot be cast to non-null type javax.sound.sampled.FloatControl");
                }
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                control.setValue(volume);
                clip.start();
            } catch (Exception e) {
                Log.info("fail");
            }
    }
}
