package cum.jesus.jesusclient.utils.threads;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.gui.clickgui.ClickGui;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.modules.other.SelfDestruct;
import cum.jesus.jesusclient.remote.Capes;
import cum.jesus.jesusclient.utils.DesktopUtils;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.opengl.Display;

import static cum.jesus.jesusclient.JesusClient.INSTANCE;

import java.io.File;
import java.util.Objects;

public class CleanUpThread extends Thread {
    public CleanUpThread() {
        super("JesusClient-Cleanup");
    }

    @Override
    public void run() {
        INSTANCE.moduleManager.getModule(SelfDestruct.class).setToggled(false);

        try {
            INSTANCE.fileManager.save();
        } catch (Exception e) {
            Logger.error("Failed to save config:");
            e.printStackTrace();
        }

        ClientCommandHandler.instance.getCommands().remove(INSTANCE.slashCommand.getCommandName());
        INSTANCE.slashCommand.getCommandAliases().forEach(obj -> {
            ClientCommandHandler.instance.getCommands().remove(obj);
        });

        Logger.debug("Cleaning up...");

        File[] tmp = INSTANCE.fileManager.tmpDir.listFiles();
        if (tmp != null) {
            for (File f : tmp) {
                f.delete();
            }
        }

        for (Command c : INSTANCE.commandManager.getCommandList()) {
            EventManager.unregister(c);
            Logger.debug("Removing command: " + c.getName());
            c = null;
        }
        INSTANCE.commandManager.getCommandList().clear();
        Logger.info("Removed all commands");

        for (Module m : INSTANCE.moduleManager.getModules()) {
            EventManager.unregister(m);
            Logger.debug("Removing module: " + m.getName());
            Objects.requireNonNull(INSTANCE.settingManager.getAllSettingsFrom(m.getName())).clear();
            m = null;
        }
        INSTANCE.moduleManager.getModules().clear();
        Logger.info("Removed all modules");

        INSTANCE.settingManager.getAllValues().clear();

        EventManager.unregister(SkyblockUtils.INSTANCE);
        EventManager.unregister(Utils.INSTANCE);
        EventManager.unregister(INSTANCE.moduleManager);
        EventManager.unregister(INSTANCE);

        Logger.debug("Removing all managers");
        INSTANCE.fileManager = null;
        INSTANCE.configManager = null;
        INSTANCE.commandManager = null;
        INSTANCE.settingManager = null;
        INSTANCE.moduleManager = null;
        INSTANCE.scriptManager = null;

        Capes.unload();

        Logger.info("Collecting garbage...");
        System.gc();

        Logger.info("Unloaded Jesus Client");

        if (SelfDestruct.addLoadCommand.getObject()) ClientCommandHandler.instance.registerCommand(INSTANCE.startCommand);

        JesusClient.clientLoaded = false;
        DesktopUtils.showDesktopNotif("JesusClient", "Successfully removed Jesus Client from your game!");
    }
}
