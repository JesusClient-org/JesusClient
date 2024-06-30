package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.CommandHandler;
import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.Entry;
import cum.jesus.jesusclient.util.ChatColor;
import cum.jesus.jesusclient.util.ChatUtils;

import java.util.List;

@Command(value = "help", description = "Provides a list of every command")
public final class HelpCommand {
    @Entry
    private void entry() {
        List<CommandHandler.RegisteredCommand> commands = JesusClient.instance.commandHandler.getCommands();
        StringBuilder sb = new StringBuilder(200);

        sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("List of available commands\n");

        for (CommandHandler.RegisteredCommand command : commands) {
            sb.append(ChatColor.GOLD).append(JesusClient.instance.config.commandPrefix.getValue()).append(command.meta.value());

            if (!command.meta.description().isEmpty())
                sb.append(" - ").append(command.meta.description());

            sb.append("\n");
        }

        sb.append("\n").append(ChatColor.GOLD).append(ChatColor.ITALIC).append("Type ").append(JesusClient.instance.config.commandPrefix.getValue()).append("cmd help for more detailed help about cmd\n");

        String[] lines = sb.toString().split("\n");

        for (String line : lines) {
            ChatUtils.sendMessage(line);
        }
    }
}
