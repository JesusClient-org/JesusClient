package cum.jesus.jesusclient.notification;

public interface NotificationRenderer {
    void show(Notification notification); // show a new notification at some point in the future

    void finish(NotificationRenderer newRenderer); // wait for any potential queued things to finish and set the renderer in NotificationManager

    void clear(); // if the renderer uses a queue, clears the queue

    void update();

    void render();
}
