package cum.jesus.jesusclient.remote;

import com.google.gson.*;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Premium {
    private static boolean userPremium;
    public static PremiumUser userInfo;

    public static class PremiumUser {
        public String discordUser;
        public String discordId;
        public String uuid;
        public boolean premium;

        public PremiumUser(String discordUser, String discordId, String uuid, boolean premium) {
            this.discordUser = discordUser;
            this.discordId = discordId;
            this.uuid = uuid;
            this.premium = premium;
        }
    }

    public static void load() {
        String url = JesusClient.backendUrl + "/api/v2/userobject?getter=uuid";
        if (HttpUtils.doesUrlExist(url)) {
            userPremium = false || JesusClient.username.equals("Jesus");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("uuid", JesusClient.compactUUID);
        String json = new Gson().toJson(payload);
        String response = HttpUtils.post(url, json);
        JsonObject temp = new Gson().fromJson(response, JsonObject.class);

        userPremium = temp.get("premium").getAsBoolean() || JesusClient.username.equals("Jesus");

        if (userPremium) {
            userInfo = new Gson().fromJson(response, PremiumUser.class);

            Logger.debug(new Gson().toJson(userInfo));
        }
    }

    public static boolean isUserPremium() {
        return userPremium;
    }

    public static String getVerType() {
        return userPremium ? "PREMIUM" : "FREE";
    }
}