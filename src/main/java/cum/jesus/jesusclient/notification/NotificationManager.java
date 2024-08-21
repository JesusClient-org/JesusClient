package cum.jesus.jesusclient.notification;

public final class NotificationManager {
    private static NotificationRenderer renderer;

    public static NotificationRenderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(NotificationRenderer renderer) {
        if (renderer != null) renderer.clear();

        if (NotificationManager.renderer != null) NotificationManager.renderer.finish(renderer);
        else setRendererReal(renderer);
    }

    public static void setRendererReal(NotificationRenderer renderer) {
        NotificationManager.renderer = renderer;
    }

    public static void notify(Notification notification) {
        if (renderer != null) renderer.show(notification);
    }

    public static void clear() {
        if (renderer != null) renderer.clear();
    }

    public static void update() {
        if (renderer != null) renderer.update();
    }

    public static void render() {
        if (renderer != null) renderer.render();
    }
}
