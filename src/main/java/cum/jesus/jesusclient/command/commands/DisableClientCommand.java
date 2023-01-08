package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class DisableClientCommand extends Command {
    public DisableClientCommand() {
        super("unload", "Unloads and mostly disables everything within Jesus Client. To re-enable Jesus Client, run the '/startjesusclient' command");
    }

    @Override
    public void run(String alias, String[] args) {
        NotificationManager.show(new Notification(NotificationType.INFO, "Stopping", "Stopping Jesus Client...", 1));
        JesusClient.INSTANCE.unLoad();
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
