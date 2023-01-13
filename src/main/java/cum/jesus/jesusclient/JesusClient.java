package cum.jesus.jesusclient;

import com.google.gson.*;
import cum.jesus.jesusclient.command.Command;
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
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.ModuleManager;
import cum.jesus.jesusclient.module.modules.other.SelfDestruct;
import cum.jesus.jesusclient.module.settings.Setting;
import cum.jesus.jesusclient.module.settings.SettingManager;
import cum.jesus.jesusclient.remote.Capes;
import cum.jesus.jesusclient.scripting.ScriptManager;
import cum.jesus.jesusclient.utils.*;
import jline.internal.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private ICommand slashCommand = new JesusSlashCommand();
    private ICommand startCommand = new EnableClientCommand();

    public void startClient() {
        ClientCommandHandler.instance.registerCommand(slashCommand);
        ClientCommandHandler.instance.getCommands().remove(startCommand.getCommandName());

        // check blacklist
        if (!HttpUtils.doesUrlExist(backendUrl + "/api/v2/blacklisted")) return;

        JsonObject payload = new JsonObject();
        payload.addProperty("uuid", JesusClient.compactUUID);
        String json = new Gson().toJson(payload);
        String response = HttpUtils.post(backendUrl + "/api/v2/blacklisted", json);
        JsonObject obj = new Gson().fromJson(response, JsonObject.class);
        blacklisted = obj.get("blacklisted").getAsBoolean();
        if (blacklisted) return;

        Launch.blackboard.forEach((string, object) -> { Logger.debug(string + ": " + object); });

        //if (!System.getProperty("user.name").equals("Somer")) return;

        // Initialize managers
        fileManager = new FileManager();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        scriptManager = new ScriptManager();

        Display.setTitle(JesusClient.CLIENT_NAME + " v" + JesusClient.CLIENT_VERSION + " - Minecraft 1.8.9");

        // loading file manager
        fileManager.init();

        fileManager.loadScripts();

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
        Capes.load();

        configManager.load();

        Logger.debug(JesusEncoding.fromString("$\u0015\u0001L(\u0001\u0002¬\u000B\u0001\u0005\u000E\u0005H\u0001LÀ@\u0001Ð•\u0010˜\u0001 (L\u0015\u000F\u0002\u0003•¬F\u000EP \t\t\u0004\u0001¯à\u0001H\u000EÐ\u0001\u0016˜c@\u0010\u0004\u000E\u0016Ø\u0002\u0010à˜@\u0090à\u0001\u000F@\u0002l¯\u000F\u0010à\u0001\u0010\u0090¯\u000B\u0001\u000E\u0010l\u0004àPl\u0010E\u0001\u0012H\u0015\t\u0010\u0015\u0015FØ\u0002\u0001(\u0005\u0001\u0010\u0010\u0002Ð\u0090àP@\u0001\u000B\u0010\u0010\u0001ØcE$\u0001•E(@\u0010\u0001\u0003\u0001\u0003\u0010\u0002\u0012˜\u0010\u0012•\u0016\u0010\u0001P\u0001\u0001\u0016\t\u0004•\u000BÐL\u0010\u0001\u0004\u0001\u0004\u0012l(H@\u0002\u0004@¯\u0003cl\u0005\u0002\u0004L\u0003\u0010À¬\u0005\u0004˜P\u0010•¬(E\u000F\u0001\u0010F¬\u0010l\u0001\u0004 \u0001\u0001\u0001àØ\u0002à \u0002\u0010\u0090\u0005$¯\u000B@\u0016\u0010\u0010c\u0004¬\u0010\u0002\u0004\u0010H\u0016\u0001\u0001˜\tH\u0012 \u0004\u0010\u0002FÀ\u000F\u0004ØÀ\u0001\u0002F \u0002$EÀE\u0001Ø\u0001\u0015càF\u0001\u0010\u0012\u0010\u0001@\u000B\u000F\u0001\u0004\u0002\u0090\u0002\u0010\u0001$Ðà@\u0004\u0010L\u0004\u0001$àc@\u0010\u0010à¯À\u0010\u0003P\u0090\t\u000E\u0004\u0010\u0010\u0001\u0001Ð\u0002 \u0016l¯\u0001\u0010à\u0002\u0002l\u0001\u000E H\u0004\u000B\u0001l(\u0010Ø@LÐ\u0015\u000E\u0010\u0003@\u0001\u0090\u0003L$E\u000E¬ \u0002\u0002\t\u0001\u0010\u0002\u0001\u0001\u0010$\u0010\u0004¯E\u0004\u0016\u0010˜¯c@¬\u0004El@\u000Fà\u000FP\u0005\u0004\u0010F\u0090\u0010\u0010\u0001\u0004\u0004\u0090F\u0003•Ðà@@\u0004c\u0010(\u0002˜L\u0004\u000B\t¯Hà\u000B\u000B$\u0004@@$\u0001\u0001 ˜à\u0002c\u0010\t\u0010Ð\u000F\u0010\u0001\u0090Ð\u0001\u0010(@H\u0012\u0004L\t\u0012(F\u0012\u0002Ø\u0002\u0003$\u000B@à\u000B\u0004H\u0015\u0010\u0010\u0001˜P\u0090\u0005\u0010c P\u0001\u0010\u0005˜\u0010ØÀ¬\u0001\u0001\u0001à\u0010EÀ@\t\u0001\u0004¯(\u0001\u000E\u0010\u0001à\u0015\u0002\u0001\u0010Ø\u0001\u0010\u0016\u0010\u0001\u0016\u0001\u0001\u000F\u0001\u0005\u0001àP\u0001\u0012•\u0002\u0001\u0001E\u0010\u0015\u0003à\u0002\u0002\u000Fc•\u0002¬\u0010Àà\u0004\u0005\u0001\u0001\u0010Ll\u0010Øl\u0001\u0001\u0002\u0010À\u0001\u0016\u0016\u0015\u0002\u0005F\u0010•Ø(••Ð˜¯H\u0001E\u0001À\u0012À$\u0010\u0010\u0015\u0012à \u0010FP\u0004¬\u0010Ð\u0010\u000E\u0002c \u0001\u0004\u0001\u0002\u0003\u000E\u0001F\tP\u0004\u0001\u0001¬\u000F\u0004L\u0090@H"));

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

        clientLoaded = false;
    }

    public void unLoad() {
        if (blacklisted) return;

        try {
            fileManager.save();
        } catch (Exception e) {
            Logger.error("Failed to save config:");
            e.printStackTrace();
        }

        ClientCommandHandler.instance.getCommands().remove(slashCommand.getCommandName());

        Logger.debug("Cleaning up...");
        Display.setTitle("Minecraft 1.8.9");

        for (Command c : commandManager.getCommandList()) {
            EventManager.unregister(c);
            Logger.debug("Removing command: " + c.getName());
            c = null;
        }
        commandManager.getCommandList().clear();
        Logger.debug("Removed all commands");

        for (Module m : moduleManager.getModules()) {
            EventManager.unregister(m);
            Logger.debug("Removing module: " + m.getName());
            Objects.requireNonNull(settingManager.getAllSettingsFrom(m.getName())).clear();
            m = null;
        }
        moduleManager.getModules().clear();
        Logger.debug("Removed all modules");

        settingManager.getAllValues().clear();

        fileManager = null;
        configManager = null;
        commandManager = null;
        settingManager = null;
        moduleManager = null;
        scriptManager = null;

        EventManager.unregister(SkyblockUtils.INSTANCE);
        EventManager.unregister(Utils.INSTANCE);
        EventManager.unregister(this);

        Capes.unload();

        Logger.debug("Collecting garbage...");
        System.gc();

        Logger.info("Unloaded Jesus Client");

        if (SelfDestruct.addLoadCommand.getObject()) ClientCommandHandler.instance.registerCommand(startCommand);

        clientLoaded = false;
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
