package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;

import java.util.ArrayList;
import java.util.List;

public class RestartClientCommand extends Command {
    public RestartClientCommand() {
        super("restart", "Restarts Jesus Client");
    }

    @Override
    public void run(String alias, String[] args) {
        JesusClient.INSTANCE.unLoad();
        JesusClient.INSTANCE.startClient();
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }
}
