package cum.jesus.jesusclient;

import cum.jesus.jesusclient.command.CommandHandler;
import cum.jesus.jesusclient.command.CommandRegistry;
import cum.jesus.jesusclient.file.FileManager;
import net.minecraft.client.Minecraft;

import java.io.File;

public class JesusClient {
    public static JesusClient instance = null;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public FileManager fileManager;
    public CommandHandler commandHandler;

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

        fileManager = new FileManager(new File(mc.mcDataDir, "jesusclient"));
        commandHandler = new CommandHandler(commandRegistry);
    }

    public void stop() {

    }
}
