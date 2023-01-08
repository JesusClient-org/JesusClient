package cum.jesus.jesusclient.notification;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {
    @NotNull
    private static LinkedBlockingQueue<Notification> pending = new LinkedBlockingQueue<>();
    @Nullable
    private static Notification current = null;

    public static void show(Notification notification) {
        pending.add(notification);
    }

    public static void clear() {
        pending.clear();
    }

    public static void removeAll(String notif) {
        pending.removeIf(n -> n.getTitle().equalsIgnoreCase(notif));
    }

    public static void update() {
        if (current != null && !current.isShown()) {
            current = null;
        }

        if (current == null && !pending.isEmpty()) {
            current = pending.poll();
            current.show();
        }
    }

    public static void render() {
        update();

        if (current != null)
            current.render();
    }
}
