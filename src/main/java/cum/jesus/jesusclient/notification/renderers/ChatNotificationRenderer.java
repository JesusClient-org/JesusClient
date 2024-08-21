package cum.jesus.jesusclient.notification.renderers;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationRenderer;
import cum.jesus.jesusclient.util.ChatColor;
import cum.jesus.jesusclient.util.ChatUtils;

public final class ChatNotificationRenderer implements NotificationRenderer {
    public static final ChatNotificationRenderer INSTANCE = new ChatNotificationRenderer();

    private ChatNotificationRenderer() {

    }

    @Override
    public void show(Notification notification) {
        switch (notification.getType()) {
            case INFO:
                ChatUtils.sendPrefixMessage(notification.getMessage());
                break;

            case WARNING:
            case ERROR:
                ChatUtils.sendPrefixMessage(notification.getMessage(), ChatColor.RED);
                break;

            case DEBUG:
                if (JesusClient.instance.devMode) {
                    ChatUtils.sendPrefixMessage(notification.getMessage());

                    Throwable e = new Throwable();
                    StackTraceElement callee = e.getStackTrace()[1];

                    ChatUtils.sendPrefixMessage(String.format("From %s.%s:%d",
                            callee.getClassName(),
                            callee.getMethodName(),
                            callee.getLineNumber()));
                }

                break;
        }
    }

    @Override
    public void finish(NotificationRenderer newRenderer) {
        NotificationManager.setRendererReal(newRenderer);
    }

    @Override
    public void clear() {

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }
}
