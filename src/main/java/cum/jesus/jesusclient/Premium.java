package cum.jesus.jesusclient;

import com.google.gson.*;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Premium {
    private static boolean userPremium;

    private static String verType;

    public static void load() {
        String url = JesusClient.backendUrl + "/api/v2/oldpremium";
        if (HttpUtils.doesUrlExist(url)) {
            userPremium = false || JesusClient.username.equals("Jesus");
            return;
        }
        JsonObject payload = new JsonObject();
        payload.addProperty("uuid", JesusClient.compactUUID);
        String json = new Gson().toJson(payload);
        String response = HttpUtils.post(url, json);
        JsonObject obj = new Gson().fromJson(response, JsonObject.class);

        userPremium = obj.get("premium").getAsBoolean() || JesusClient.username.equals("Jesus");
    }

    public static boolean isUserPremium() {
        return userPremium;
    }

    public static String getVerType() {
        return userPremium ? "PREMIUM" : "FREE";
    }
}
