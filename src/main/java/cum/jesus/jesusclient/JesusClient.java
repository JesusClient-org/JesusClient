package cum.jesus.jesusclient;

import com.google.gson.Gson;
import cum.jesus.jesusclient.command.CommandHandler;
import cum.jesus.jesusclient.config.ClientConfig;
import cum.jesus.jesusclient.config.ConfigManager;
import cum.jesus.jesusclient.event.EventManager;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.module.ModuleHandler;
import cum.jesus.jesusclient.module.ModuleRegistry;
import cum.jesus.jesusclient.script.ScriptManager;
import cum.jesus.jesusclient.script.runtime.listeners.ClientListener;
import cum.jesus.jesusclient.setting.SettingManager;
import cum.jesus.jesusclient.util.User;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;

public class JesusClient {
    public static Minecraft mc;
    public static Gson gson = new Gson();

    public static JesusClient instance = null;
    private static boolean loaded = false;

    public ConfigManager configManager;
    public SettingManager settingManager;
    public CommandHandler commandHandler;
    public ModuleHandler moduleHandler;

    public ClientConfig config;

    public boolean devMode = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || User.username.equals("JesusTouchMe") || FileManager.hasFile("developer");

    private JesusClient() {
    }

    public static void init() {
        if (instance != null) {
            throw new RuntimeException("Initializing JesusClient multiple times");
        }

        instance = new JesusClient();
        mc = Minecraft.getMinecraft();
    }

    public static boolean isLoaded() {
        return instance != null && loaded;
    }

    public void start() {
        ModuleRegistry moduleRegistry = new ModuleRegistry();

        configManager = new ConfigManager();
        settingManager = new SettingManager();
        commandHandler = new CommandHandler();
        moduleHandler = new ModuleHandler(moduleRegistry);

        FileManager.clearTmpDir();

        config = new ClientConfig();

        settingManager.registerObject("Client", config);

        commandHandler.addCommands();
        moduleHandler.addModules();

        if (devMode) {
            commandHandler.addDevCommands();
            moduleHandler.addDevModules();
        }

        EventManager.register(moduleHandler);

        configManager.load();

        ScriptManager.setup();
        ScriptManager.entryPass();

        EventManager.register(ClientListener.INSTANCE);

        EventManager.cleanRegistry(true);

        loaded = true;
    }

    public void stop() {
        configManager.save();
        FileManager.clearTmpDir();

        loaded = false;
    }
}
