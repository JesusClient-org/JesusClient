package cum.jesus.jesusclient.scripting.runtime.jesusclient.utils;

import cum.jesus.jesusclient.utils.DesktopUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class WrapperDesktopUtils {
    public boolean openWebpage(String url) {
        try {
            return DesktopUtils.openWebpage(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void showDesktopNotif(String caption, String text) {
        DesktopUtils.showDesktopNotif(caption, text);
    }
}
