package cum.jesus.jesusclient.command.commands.dev;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpDevCommand extends Command {
    public HttpDevCommand() {
        super("http", "Sends http requests");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args[0].equalsIgnoreCase("post") && args.length < 3) {
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "post <url> [<jsonData>]");
        } else if (args[0].equalsIgnoreCase("get") && args.length != 2) {
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "get [<url>]");
        }

        if (args[0].equalsIgnoreCase("get")) {
            (new Thread(() -> {
                ChatUtils.sendPrefixMessage(HttpUtils.get(args[1]));
            }, "JesusClient-http-get-devcmd")).start();
        } else if (args[0].equalsIgnoreCase("post")) {
            (new Thread(() -> {
                StringBuilder builder = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    builder.append(args[i]);

                    if (i != args.length - 1) {
                        builder.append(" ");
                    }
                }
                String jsonData = builder.toString();

                ChatUtils.sendPrefixMessage(HttpUtils.post(args[1], jsonData));
            }, "JesusClient-http-post-devcmd")).start();
        } else {
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "<get/post> [<url>] [jsonData]");
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return arg == 1 ? Arrays.asList("get", "post") : new ArrayList<>();
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }
}
