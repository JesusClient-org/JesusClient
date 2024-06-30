package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.Entry;
import cum.jesus.jesusclient.command.annotations.SubCommand;
import cum.jesus.jesusclient.util.DesktopUtils;

import java.net.MalformedURLException;
import java.net.URL;

@Command(value = "discord", description = "Join the JesusClient Discord server")
public final class DiscordCommand {
    @Entry
    private void entry() throws CommandException {
        try {
            DesktopUtils.openWebpage(new URL("https://discord.gg/t3HQAGXRzE"));
        } catch (MalformedURLException e) {
            throw new CommandException("Failed to join JesusClient Discord server.", e);
        }
    }
}
