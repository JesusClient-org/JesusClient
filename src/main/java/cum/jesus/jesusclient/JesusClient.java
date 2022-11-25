package cum.jesus.jesusclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.command.CommandManager;
import cum.jesus.jesusclient.command.commands.JesusSlashCommand;
import cum.jesus.jesusclient.config.ConfigManager;
import cum.jesus.jesusclient.files.FileManager;
import cum.jesus.jesusclient.module.ModuleManager;
import cum.jesus.jesusclient.module.settings.SettingManager;
import cum.jesus.jesusclient.remote.Capes;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JesusClient {
    // Metadata
    @NotNull
    public static final String CLIENT_NAME = "Jesus Client";
    @NotNull
    public static final String CLIENT_AUTHOR = "JesusTouchMe";
    public static final double CLIENT_VERSION_NUMBER = 0.1;
    @NotNull
    public static final String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "-" + Premium.getVerType();
    @NotNull
    public static final String CLIENT_INITIALS;

    public static JesusClient INSTANCE;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static String username = mc.getSession().getUsername();
    public static String uuid = mc.getSession().getProfile().getId().toString();
    public static String compactUUID = uuid.replace("-","");
    public static String ssid = RandomStringUtils.random(mc.getSession().getSessionID().length(), true, true);

    public static JsonObject backend;

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

    public FileManager fileManager;
    public ConfigManager configManager;
    public CommandManager commandManager;
    public SettingManager settingManager;
    public ModuleManager moduleManager;

    public JesusClient() {
        INSTANCE = this;
    }

    public void startClient() {
        backend = (JsonObject) WebUtils.getJsonFromUrl("https://jesustouchme.ga/api/v1/childp/jessepinkman.json");

        // check blacklist
        JsonArray blacklist = backend.get("blacklist").getAsJsonArray();
        ArrayList<String> blacklistArray = new ArrayList<>();
        Iterator<JsonElement> iterator = blacklist.iterator();
        int c = 0;
        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            blacklistArray.add(next.getAsString());
            c++;
        }
        boolean uuidInList = blacklistArray.stream().anyMatch(s -> s.equals(compactUUID));
        if (uuidInList) return;

        Premium.load();

        // Initialize managers
        fileManager = new FileManager();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();

        Display.setTitle(CLIENT_NAME + " v" + CLIENT_VERSION);

        // loading file manager
        try {
            fileManager.init();
            fileManager.loadFirstTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add commands
        ClientCommandHandler.instance.registerCommand(new JesusSlashCommand());
        if (commandManager.addCommands()) Logger.info("Loaded command manager");

        // Add modules
        if (moduleManager.addModules()) Logger.info("Loaded module manager");

        // Load capes
        Capes.load();

        configManager.load();
    }

    public void stopClient() {
        Logger.info("Stopping client");

        try {
            fileManager.save();
        } catch (Exception e) {
            Logger.error("Failed to save config:");
            e.printStackTrace();
        }
    }

    public static void sendMessage(Object message) {
        mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(message.toString()));
    }

    public static void sendPrefixMessage(Object message) {
        mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText("\u00A78[\u00A74Jesus Client\u00A78] \u00A77" + message));
    }
}
