package cum.jesus.jesusclient.remote;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.slaves.WorkerPool;
import cum.jesus.jesusclient.utils.slaves.jobs.DownloadCapeJob;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Capes {
    private static WorkerPool pool = new WorkerPool(4);
    private static int id = 1;

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

               id++;
            }

            for (Map.Entry<String, JsonElement> owner : jsonOwners.entrySet()) {
                playerCapes.put(owner.getKey(), owner.getValue().getAsString());
            }

            //JesusClient.Log.debug(getCape(JesusClient.compactUUID));
        } catch (Exception e) {
            Logger.error("Could not download capes");
            e.printStackTrace();
        }

        pool.kill();
    }

    public static void unload() {
        capes.clear();
        playerCapes.clear();
    }

    private static ResourceLocation capeFromFile(String capeName, String capeUrl) {
        try {
            File file = new File(capeDir, capeName + ".png");
            AtomicReference<ResourceLocation> rl = new AtomicReference<>();
            if (!file.exists()) {
                pool.queueJob(new DownloadCapeJob(id, capeUrl, file, (texture) -> {
                    JesusClient.mc.addScheduledTask(() -> {
                        try {
                            rl.set(JesusClient.mc.getTextureManager().getDynamicTextureLocation("jesusclient", texture));
                        } catch (Exception e) {
                            Logger.error("Failed to register dynamic texture: " + capeUrl);
                            e.printStackTrace();
                        }
                    });
                }));
            } else {
                BufferedImage image = ImageIO.read(file);
                DynamicTexture texture = new DynamicTexture(image);
                rl.set(JesusClient.mc.getTextureManager().getDynamicTextureLocation("jesusclient", texture));
            }
            return rl.get();
        } catch (IOException e) {
            Logger.error("Failed to load the funny cape");
            e.printStackTrace();
        }
        return null;
    }
}
