package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("discord", "Join the Jesus Client Discord", 0, 0, new String[0]);
    }

    @Override
    public void onCall(String[] args) {
        try {
            Utils.openWebpage(new URL("https://discord.gg/tjpg7mHjn2"));
        } catch (MalformedURLException e) {
            JesusClient.sendPrefixMessage("Failed to open Jesus Client server");
            e.printStackTrace();
        }
    }
}
