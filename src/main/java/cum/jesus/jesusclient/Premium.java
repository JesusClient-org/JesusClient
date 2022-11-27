package cum.jesus.jesusclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import cum.jesus.jesusclient.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Premium {
    private static boolean userPremium;

    private static String verType;

    public static void load() {
        JsonArray whitelist = JesusClient.backend.get("whitelist").getAsJsonArray();
        ArrayList<String> whitelistArray = new ArrayList<>();

        for (JsonElement next : whitelist) {
            whitelistArray.add(next.getAsString());
        }

        boolean uuidInList = whitelistArray.stream().anyMatch(s -> s.equals(JesusClient.compactUUID));

        if (uuidInList || JesusClient.username.equals("Jesus")) {
            userPremium = true;

            Logger.info("Using premium version. Enjoy");
        } else {
            userPremium = false;

            Logger.info("Using free/public version");
        }
    }

    public static boolean isUserPremium() {
        return userPremium;
    }

    public static String getVerType() {
        return userPremium ? "PREMIUM" : "PUBLIC";
    }
}
