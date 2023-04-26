package cum.jesus.jesusclient;

import com.google.gson.*;
import cum.jesus.jesusclient.command.CommandManager;
import cum.jesus.jesusclient.command.commands.EnableClientCommand;
import cum.jesus.jesusclient.command.commands.JesusSlashCommand;
import cum.jesus.jesusclient.config.ConfigManager;
import cum.jesus.jesusclient.events.GameTickEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.files.FileManager;
import cum.jesus.jesusclient.files.JesusEncoding;
import cum.jesus.jesusclient.module.ModuleManager;
import cum.jesus.jesusclient.module.settings.SettingManager;
import cum.jesus.jesusclient.remote.Capes;
import cum.jesus.jesusclient.scripting.LibraryScript;
import cum.jesus.jesusclient.scripting.Script;
import cum.jesus.jesusclient.scripting.ScriptManager;
import cum.jesus.jesusclient.utils.*;
import cum.jesus.jesusclient.utils.threads.CleanUpThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.ICommand;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class JesusClient {
    // Metadata
    @NotNull
    public static final String CLIENT_NAME = "Jesus Client";
    @NotNull
    public static final String CLIENT_AUTHOR = "JesusTouchMe";
    public static final String CLIENT_VERSION_NUMBER = "2.0";
    @NotNull
    public static String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "-DEV";
    @NotNull
    public static final String CLIENT_INITIALS;

    public static JesusClient INSTANCE;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static String username = mc.getSession().getUsername();
    public static String uuid = mc.getSession().getProfile().getId().toString();
    public static String compactUUID = uuid.replace("-","");
    public static String ssid = "token:" +  RandomStringUtils.random(mc.getSession().getToken().length(), true, true) + ":" +  RandomStringUtils.random(mc.getSession().getPlayerID().length(), true, true);

    public static String backendUrl = "http://62.107.137.187:6969";

    public static GuiScreen display = null;

    public boolean blacklisted;
    public static boolean devMode = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || username.equals("JesusTouchMe");
    public static boolean init;
    public static boolean clientLoaded;

    static {
        List<Character> chars = new ArrayList<>();

        for (char c : CLIENT_NAME.toCharArray())
            if (Character.toUpperCase(c) == c) chars.add(c);

        char[] c = new char[chars.size()];

        for (int i = 0; i < chars.size(); i++) {
            c[i] = chars.get(i);
        }

        CLIENT_INITIALS = new String(c);
    }

    public JesusClientNatives natives;
    public FileManager fileManager;
    public ConfigManager configManager;
    public CommandManager commandManager;
    public SettingManager settingManager;
    public ModuleManager moduleManager;
    public ScriptManager scriptManager;

    public JesusClient() {
        init = false;
        clientLoaded = false;
        INSTANCE = this;
    }

    public ICommand slashCommand = new JesusSlashCommand();
    public ICommand startCommand = new EnableClientCommand();

    public void startClient() {
        ClientCommandHandler.instance.registerCommand(slashCommand);
        ClientCommandHandler.instance.getCommands().remove(startCommand.getCommandName());
        startCommand.getCommandAliases().forEach(obj -> {
            ClientCommandHandler.instance.getCommands().remove(obj);
        });

        Launch.blackboard.forEach((string, object) -> { Logger.debug(string + ": " + object); });

        //if (!System.getProperty("user.name").equals("Somer")) return;

        // Initialize managers
        natives = new JesusClientNatives();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        scriptManager = new ScriptManager();

        // check blacklist
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("uuid", JesusClient.compactUUID);
            String json = new Gson().toJson(payload);
            String response = HttpUtils.post(backendUrl + "/api/v2/blacklisted", json);
            JsonObject obj = new Gson().fromJson(response, JsonObject.class);
            blacklisted = obj.get("blacklisted").getAsBoolean();

            File blacklist = new File(fileManager.clientDir, "backend.jesus");
            if (!blacklist.exists()) blacklist.createNewFile();

            String text = JesusEncoding.toString(blacklisted ? "user is blacklisted lol" : "user is not blacklisted");

            Files.write(blacklist.toPath(), text.getBytes());
        } catch (Exception e) {
            File blacklist = new File(fileManager.clientDir, "backend.jesus");
            if (!blacklist.exists()) blacklisted = true;

            try {
                String text = JesusEncoding.fromString(new String(Files.readAllBytes(blacklist.toPath())));

                blacklisted = text.equals("user is blacklisted lol");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (blacklisted) {
            natives = null;
            fileManager = null;
            configManager = null;
            commandManager = null;
            settingManager = null;
            moduleManager = null;
            scriptManager = null;

            ClientCommandHandler.instance.getCommands().remove(slashCommand.getCommandName());
            slashCommand.getCommandAliases().forEach(obj -> {
                ClientCommandHandler.instance.getCommands().remove(obj);
            });

            return;
        }

        Display.setTitle(JesusClient.CLIENT_NAME + " v" + JesusClient.CLIENT_VERSION + " - Minecraft 1.8.9");

        // loading file manager
        fileManager.init();
        fileManager.loadScripts();

        natives.init();

        // Add commands
        if (commandManager.addCommands()) Logger.info("Loaded command manager");

        // Add modules
        if (moduleManager.addModules()) Logger.info("Loaded module manager");



        // load first time
        try {
            fileManager.loadFirstTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventManager.register(SkyblockUtils.INSTANCE);
        EventManager.register(Utils.INSTANCE);
        EventManager.register(this);

        // Load capes
        long startTime = System.currentTimeMillis();
        Capes.load();
        long endTime = System.currentTimeMillis();

        Logger.debug("Capes took " + (endTime - startTime) + " milliseconds to complete");

        configManager.load();

        if (devMode) DesktopUtils.showDesktopNotif("Jesus Client", "Jesus Client has been loaded");

        init = true;
        clientLoaded = true;
    }

    public void stopClient() {
        if (blacklisted) return;

        try {
            fileManager.save();
        } catch (Exception e) {
            Logger.error("Failed to save config:");
            e.printStackTrace();
        }

        natives.stop();

        File[] tmp = fileManager.tmpDir.listFiles();
        if (tmp != null) {
            for (File f : tmp) {
                f.delete();
            }
        }

        clientLoaded = false;
    }

    public void unLoad() {
        if (blacklisted) return;

        Display.setTitle("Minecraft 1.8.9");

        CleanUpThread cleanUpThread = new CleanUpThread();
        cleanUpThread.start();
    }

    @EventTarget
    public void tick(GameTickEvent event) {
        if (event.getEventType() != EventType.PRE) return;
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        if (display != null) {
            try {
                mc.displayGuiScreen(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
            display = null;
        }
    }
}
