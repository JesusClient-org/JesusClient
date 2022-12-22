package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.PacketEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import jline.internal.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
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
import java.util.ArrayList;
import java.util.List;

import static cum.jesus.jesusclient.JesusClient.mc;

public class Utils {
    public static boolean onHypixel = false;

    @EventTarget
    public void packetShit(PacketEvent event) {
        if (event.getEventType() != EventType.RECIEVE) return;

        if (!onHypixel && event.getPacket() instanceof S3FPacketCustomPayload && ((S3FPacketCustomPayload) event.getPacket()).getChannelName() == "MC|Brand") {
            if (((S3FPacketCustomPayload) event.getPacket()).getBufferData().readStringFromBuffer((int)Short.MAX_VALUE).toLowerCase().contains("hypixel")) {
                onHypixel = true;
            }
        }
    }

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

    public static void throwSlot(int slot) {
        ItemStack curInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
        if (curInSlot != null)
            if (curInSlot.getDisplayName().contains("Snowball")) {
                int ss = curInSlot.stackSize;
                for (int i = 0; i < ss; i++) {
                    mc.thePlayer.inventory.currentItem = slot;
                    mc.playerController.sendUseItem((EntityPlayer) mc.thePlayer, (World) mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot));
                }
            } else {
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.sendUseItem((EntityPlayer) mc.thePlayer, (World) mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot));
            }
    }

    public static int getAvailableHotbarSlot(String name) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.getStackInSlot(i);
            if (is == null || is.getDisplayName().contains(name))
                return i;
        }
        return -1;
    }

    public static java.util.List<Integer> getAllSlots(int throwSlot, String name) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 9; i < 44; i++) {
            ItemStack is = ((Slot) mc.thePlayer.inventoryContainer.inventorySlots.get(i)).getStack();
            if (is != null && is.getDisplayName().contains(name) && i - 36 != throwSlot)
                ret.add(Integer.valueOf(i));
        }
        return ret;
    }

    public static String getInventoryName() {
        if(mc.thePlayer == null || mc.theWorld == null) return "null";
        return mc.thePlayer.openContainer.inventorySlots.get(0).inventory.getName();
    }
}
