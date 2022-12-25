package cum.jesus.jesusclient.scripting.runtime.deobfedutils;

import cum.jesus.jesusclient.utils.DesktopUtils;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ScriptDesktopUtils {
    public static boolean openWebpage(URI uri) {
        return DesktopUtils.openWebpage(uri);
    }

    public static boolean openWebpage(URL url) {
        return DesktopUtils.openWebpage(url);
    }

    public static boolean openWebpage(String url) {
        try {
            return DesktopUtils.openWebpage(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showDesktopNotif(String caption, String text) {
        DesktopUtils.showDesktopNotif(caption, text);
    }
}
