package cum.jesus.jesusclient.scripting.runtime.utils;

import cum.jesus.jesusclient.utils.HttpUtils;

public class ScriptHttpUtils {
    public static String get(String url) {
        return HttpUtils.get(url);
    }

    /**
     * Posts JSON data to a URL
     *
     * @return Returns the {@link String} response or null if something goes wrong
     */
    public static String post(String url, String jsonData) {
        return HttpUtils.post(url, jsonData);
    }

    public static boolean doesUrlExist(String url) {
        return doesUrlExist(url);
    }
}
