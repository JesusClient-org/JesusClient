package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.DesktopUtils;
import cum.jesus.jesusclient.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("discord", "Join the Jesus Client Discord");
    }

    @Override
    public void run(String alias, String[] args) {
        try {
            DesktopUtils.openWebpage(new URL("https://discord.gg/tjpg7mHjn2"));
        } catch (MalformedURLException e) {
            ChatUtils.sendPrefixMessage("Failed to open Jesus Client server");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
