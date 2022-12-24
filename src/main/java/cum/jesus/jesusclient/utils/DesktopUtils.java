package cum.jesus.jesusclient.utils;

import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DesktopUtils {
    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showDesktopNotif(String caption, String text) {
        try{
            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            // If you want to create an icon in the system tray to preview
            Image image = Toolkit.getDefaultToolkit().createImage(new ResourceLocation("jesusclient", "jesus.png").getResourceDomain());
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Jesus Client");
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("Jesus Client");
            tray.add(trayIcon);

            // Display info notification:
            trayIcon.displayMessage("Hello, World", "Java Notification Demo", TrayIcon.MessageType.INFO);
            // Error:
            // trayIcon.displayMessage("Hello, World", "Java Notification Demo", MessageType.ERROR);
            // Warning:
            // trayIcon.displayMessage("Hello, World", "Java Notification Demo", MessageType.WARNING);
        }catch(Exception ex){
            System.err.print(ex);
        }
    }
}
