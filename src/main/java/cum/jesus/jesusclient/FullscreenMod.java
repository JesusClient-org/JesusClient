package cum.jesus.jesusclient;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.remote.Updater;
import cum.jesus.jesusclient.utils.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

@Mod(modid="sk1er_fullscreen", name = "Fullscreen Mod", version="2.0", acceptedMinecraftVersions="[1.8.9]")
public class FullscreenMod {
    public static final String MODID = "sk1er_fullscreen";
    public static final String VERSION = "2.0";
    boolean lastFullscreen = false;
    public static File jarFile = null;
    public static final File modDir = new File(JesusClient.INSTANCE.mc.mcDataDir + "/mods");
    public static final File updaterExe = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "jesusupdat.exe");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (JesusClient.devMode) {
            Logger.info("You are running in dev mode");
            return;
        }
        Logger.info("Checking updates");
        jarFile = event.getSourceFile();

        if (!updaterExe.exists()) {
            try {
                Files.copy(new URL(JesusClient.backendUrl + "/download/updater").openStream(), updaterExe.toPath());
            } catch (IOException e) {}
        }

        Updater.loadUpdate();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Logger.info("Loaded Jesus Client fake FullscreenMod");
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        boolean fullScreenNow = Minecraft.getMinecraft().isFullScreen();
        if (this.lastFullscreen != fullScreenNow) {
            this.fix(fullScreenNow);
            this.lastFullscreen = fullScreenNow;
        }
    }

    public void fix(boolean fullscreen) {
        try {
            if (fullscreen) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                Display.setLocation(0, 0);
                Display.setFullscreen(false);
                Display.setResizable(false);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Display.setDisplayMode(new DisplayMode(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight));
                Display.setResizable(true);
            }
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }
    }
}
