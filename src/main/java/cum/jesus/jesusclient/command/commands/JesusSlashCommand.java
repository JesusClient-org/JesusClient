package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class JesusSlashCommand extends CommandBase {
    public String getCommandName() {
        return "jesus";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    public List<String> getCommandAliases() {
        return Arrays.asList("jesusclient");
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (JesusClient.INSTANCE.blacklisted)
            JesusClient.sendPrefixMessage("you're black");
        else
            JesusClient.sendPrefixMessage("the command is " + Gui.prefix.getObject() + "jesus");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}