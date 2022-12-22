package cum.jesus.jesusclient.command.commands.premium;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;
import jline.internal.Log;

import java.util.ArrayList;
import java.util.List;

public class SpamWebhookPremiumCommand extends Command {
    public SpamWebhookPremiumCommand() {
        super("spamwebhook", "Spams a webhook");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 3)
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + " [<url>] [<amount>] [<message>]");

        if (!args[0].startsWith("https://discord.com/"))
            throw new CommandException("The provided url is not a Discord url");

        if (!HttpUtils.doesUrlExist(args[0]))
            throw new CommandException("The provided webhook does not exist");

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]);
        }

        String msg = sb.toString();

        JsonObject payload = new JsonObject();
        payload.addProperty("content", msg);
        String finalMsg = new Gson().toJson(payload);

        Thread senderThread = new Thread(() -> {
            for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                String res = HttpUtils.post(args[0], finalMsg);
                if (res == null) res = "no res";

                if (res != "no res") {
                    JsonObject resJson = new Gson().fromJson(res, JsonObject.class);

                    boolean global = resJson.get("global").getAsBoolean();
                    String message = resJson.get("message").getAsString();

                    if (!global && message.toLowerCase().contains("rate limited")) {
                        Logger.warn(Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + ": Rate limited");
                    }
                }
            }

            ChatUtils.sendPrefixMessage("Finished spamming webhook");
        }, "Webhook Spammer (" + args[1] + ")");

        senderThread.start();
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean isPremiumOnly() {
        return true;
    }
}
