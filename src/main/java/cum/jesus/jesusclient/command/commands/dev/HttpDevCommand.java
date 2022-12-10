package cum.jesus.jesusclient.command.commands.dev;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

public class HttpDevCommand extends Command {
    public HttpDevCommand() {
        super("http", "Sends http requests", 2, 3, new String[] { "type", "url", "json data" });
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }

    @Override
    public void onCall(String[] args) {
        String type = args[1];
        String url = args[2];
        if (type.equals("get")) {
            (new Thread(() -> {
                JesusClient.sendPrefixMessage(HttpUtils.get(url));
            })).start();
        } else if (type.equals("post")) {
            (new Thread(() -> {
                JesusClient.sendPrefixMessage(HttpUtils.post(url, args[3]));
            })).start();
        } else {
            incorrectArgs();
        }
    }
}
