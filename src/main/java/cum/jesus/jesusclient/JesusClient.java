package cum.jesus.jesusclient;

import cum.jesus.jesusclient.command.CommandHandler;
import cum.jesus.jesusclient.config.ClientConfig;
import cum.jesus.jesusclient.config.ConfigManager;
import cum.jesus.jesusclient.event.EventManager;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.module.ModuleHandler;
import cum.jesus.jesusclient.module.ModuleRegistry;
import cum.jesus.jesusclient.setting.SettingManager;
import cum.jesus.jesusclient.setting.User;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;

import java.io.File;

public class JesusClient {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static JesusClient instance = null;
    private static boolean loaded = false;

    public FileManager fileManager;
    public ConfigManager configManager;
    public SettingManager settingManager;
    public CommandHandler commandHandler;
    public ModuleHandler moduleHandler;

    public ClientConfig config;

    public boolean devMode = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || User.username.equals("JesusTouchMe");

    private JesusClient() {
    }

    public static void init() {
        if (instance != null) {
            throw new RuntimeException("Initializing JesusClient multiple times");
        }

        instance = new JesusClient();
    }

    public static boolean isLoaded() {
        return instance != null && loaded;
    }

    public void start() {
        ModuleRegistry moduleRegistry = new ModuleRegistry();

        fileManager = new FileManager(new File(mc.mcDataDir, "jesusclient"));
        configManager = new ConfigManager();
        settingManager = new SettingManager();
        commandHandler = new CommandHandler();
        moduleHandler = new ModuleHandler(moduleRegistry);

        if (!devMode && fileManager.hasFile("developer"))
            devMode = true;

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

        EventManager.cleanRegistry(true);

        loaded = true;
    }

    public void stop() {
        configManager.save();
        fileManager.clearTmpDir();

        loaded = false;
    }
}
