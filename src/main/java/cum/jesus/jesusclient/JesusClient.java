package cum.jesus.jesusclient;

import cum.jesus.jesusclient.command.CommandHandler;
import cum.jesus.jesusclient.command.CommandRegistry;
import cum.jesus.jesusclient.config.ClientConfig;
import cum.jesus.jesusclient.config.ConfigManager;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.module.ModuleHandler;
import cum.jesus.jesusclient.module.ModuleRegistry;
import cum.jesus.jesusclient.setting.SettingManager;
import cum.jesus.jesusclient.util.Logger;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.Objects;

public class JesusClient {
    public static JesusClient instance = null;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public FileManager fileManager;
    public ConfigManager configManager;
    public SettingManager settingManager;
    public CommandHandler commandHandler;
    public ModuleHandler moduleHandler;

    public ClientConfig config = new ClientConfig();

    private JesusClient() {

    }

    public static void init() {
        if (instance != null) {
            throw new RuntimeException("Initializing JesusClient multiple times");
        }

        instance = new JesusClient();
    }

    public void start() {
        CommandRegistry commandRegistry = new CommandRegistry();
        ModuleRegistry moduleRegistry = new ModuleRegistry();

        fileManager = new FileManager(new File(mc.mcDataDir, "jesusclient"));
        configManager = new ConfigManager();
        settingManager = new SettingManager();
        commandHandler = new CommandHandler(commandRegistry);
        moduleHandler = new ModuleHandler(moduleRegistry);

        settingManager.registerObject("client", config);

        configManager.load(Objects.requireNonNull(fileManager.get("client.jesus")));

        Logger.info(config.test.getValue());
    }

    public void stop() {

    }
}
