package cum.jesus.jesusclient.remote;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Capes {
    public static HashMap<String, String> playerCapes = new HashMap<>();
    public static HashMap<String, ResourceLocation> capes = new HashMap<>();

    private static final File capeDir = new File(JesusClient.INSTANCE.fileManager.cacheDir, "capes");

    public static ResourceLocation getCape(String uuid) {
        String capeName = playerCapes.get(uuid);
        if(capeName == null) return null;

        return capes.get(capeName);
    }

    public static void load() {
        Logger.info("Loading capes...");
        capeDir.mkdirs();

        try {
            String capeJson = HttpUtils.get(JesusClient.backendUrl + "/api/v2/capedata");

            JsonObject json = new Gson().fromJson(capeJson, JsonObject.class);
            JsonObject jsonCapes = json.get("capes").getAsJsonObject();
            JsonObject jsonOwners = json.get("owners").getAsJsonObject();

            for (Map.Entry<String, JsonElement> e : jsonCapes.entrySet()) {
                String name = e.getKey();
                String url = e.getValue().getAsString();

                Logger.info("Loading cape: " + name + " from cache");

               capes.put(name, capeFromFile(name, url));
            }

            for (Map.Entry<String, JsonElement> owner : jsonOwners.entrySet()) {
                playerCapes.put(owner.getKey(), owner.getValue().getAsString());
            }

            //JesusClient.Log.debug(getCape(JesusClient.compactUUID));
        } catch (Exception e) {
            Logger.error("Could not download capes");
            e.printStackTrace();
        }
    }

    private static ResourceLocation capeFromFile(String capeName, String capeUrl) {
        try {
            File file = new File(capeDir, capeName + ".png");
            if (!file.exists()) Files.copy(new URL(capeUrl).openStream(), file.toPath());

            return JesusClient.mc.getTextureManager().getDynamicTextureLocation("jesusclient", new DynamicTexture(ImageIO.read(file)));
        } catch (IOException e) {
            Logger.error("Failed to load the funny cape");
            e.printStackTrace();
        }
        return null;
    }
}
