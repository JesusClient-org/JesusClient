package cum.jesus.jesusclient.scripting.runtime.jesusclient.utils;

import cum.jesus.jesusclient.utils.HttpUtils;

public class WrapperHttpUtils {
    public String get(String url) {
        return HttpUtils.get(url);
    }

    public String post(String url, String jsonString) {
        return HttpUtils.post(url, jsonString);
    }

    public boolean doesUrlExist(String url) {
        return HttpUtils.doesUrlExist(url);
    }
}
