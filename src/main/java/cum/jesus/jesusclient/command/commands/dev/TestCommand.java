package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.SubCommand;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationRenderer;
import cum.jesus.jesusclient.notification.NotificationType;
import cum.jesus.jesusclient.notification.renderers.BasicNotificationRenderer;
import cum.jesus.jesusclient.notification.renderers.ChatNotificationRenderer;

@Command(value = "test", description = "Developer command for testing various systems in JesusClient")
public final class TestCommand {
    @SubCommand(description = "Test every notification renderer")
    private void notifications() {
        NotificationRenderer savedRenderer = NotificationManager.getRenderer();
        NotificationRenderer[] renderers = {
                ChatNotificationRenderer.INSTANCE,
                BasicNotificationRenderer.INSTANCE,
        };
        Notification[] notifications = {
                new Notification("Premium Only", "This feature is only available\nto premium users"),
                new Notification("Testing ensures quality :speaking_head:"),
                new Notification(NotificationType.ERROR, "Error!", "Silly error occurred when opening rickroll", 10)
        };

        for (NotificationRenderer renderer : renderers) {
            NotificationManager.setRenderer(renderer);

            for (Notification notification : notifications) {
                NotificationManager.notify(notification);
            }
        }

        NotificationManager.setRenderer(savedRenderer);
    }
}
