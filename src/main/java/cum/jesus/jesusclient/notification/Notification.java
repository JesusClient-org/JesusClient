package cum.jesus.jesusclient.notification;

import org.lwjgl.Sys;

public final class Notification {
    private NotificationType type;
    private String title;
    private String message;
    private long start;

    private long fadeIn;
    private long fadeOut;
    private long end;

    public Notification(NotificationType type, String title, String message, int length) {
        this.type = type;
        this.title = title;
        this.message = message;

        fadeIn = 200L * length;
        fadeOut = fadeIn + 500L * length;
        end = fadeOut + fadeIn;
    }

    public Notification(NotificationType type, String title, String message) {
        this(type, title, message, 4);
    }

    public Notification(NotificationType type, String message) { // for chat
        this(type, "", message);
    }

    public Notification(String title, String message) {
        this(NotificationType.INFO, title, message);
    }

    public Notification(String message) {
        this(NotificationType.INFO, message);
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public long getFadeIn() {
        return fadeIn;
    }

    public long getFadeOut() {
        return fadeOut;
    }

    public long getEnd() {
        return end;
    }
}
