package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationType;
import cum.jesus.jesusclient.utils.ChatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class EnableClientCommand extends CommandBase {
    public String getCommandName() {
        return "startjesusclient";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "Used for starting Jesus Client in case it has been unloaded";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (JesusClient.INSTANCE.blacklisted) {
            ChatUtils.sendPrefixMessage("you're black");
            return;
        }

        JesusClient.INSTANCE.startClient();
        NotificationManager.show(new Notification(NotificationType.INFO, "Started Client", "Successfully started Jesus Client", 2));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
