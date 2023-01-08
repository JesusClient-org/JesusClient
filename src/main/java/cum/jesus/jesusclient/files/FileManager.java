package cum.jesus.jesusclient.files;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Hud;
import cum.jesus.jesusclient.utils.Logger;
import net.minecraft.launchwrapper.ITweaker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileManager {
    public final File clientDir = new File(JesusClient.INSTANCE.mc.mcDataDir, JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""));
    public final File backupDir = new File(clientDir, "config-backups");
    public final File cacheDir = new File(clientDir, "CACHE");
    public final File scriptDir = new File(clientDir, "scripts");

    public final File configFile = new File(clientDir, "jesusconfig.json");
    private final File firstTimeFile = new File(clientDir, "firsttime.txt");

    public static final File modDir = new File(JesusClient.INSTANCE.mc.mcDataDir + "/mods");
    public static final File updaterExe = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "jesusupdat.exe");

    public static File srcJar = null;

    public void save() throws Exception {
        if (!configFile.exists() && !configFile.createNewFile())
            throw new IOException("Failed to create config file");

        Files.write(formatJson(JesusClient.INSTANCE.configManager.toJsonObject().toString()).getBytes(StandardCharsets.UTF_8), configFile);
    }

    public void init() {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        backupDir.mkdirs();

        // download external needed assets
        (new Thread(() -> {

        }, "JesusClient-Asset-Downloader")).start();

        // delete the oldest backup if there's more than 25
        File[] backups = backupDir.listFiles();
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;
        if (backups != null && backups.length >= 25) {
            // delete oldest files after theres more than 25 backup files
            for (File f : backups) {
                if (f.lastModified() < oldestDate) {
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }

            if (oldestFile != null) {
                Logger.info("Deleted config backup: " + oldestFile.getName());
                //noinspection ResultOfMethodCallIgnored
                oldestFile.delete();
            }
        }
    }

    public static void doUpdater() throws URISyntaxException {
        srcJar = new File(JesusClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Logger.debug(srcJar.getAbsolutePath());

        if (!updaterExe.exists()) {
            try {
                java.nio.file.Files.copy(new URL(JesusClient.backendUrl + "/download/updater").openStream(), updaterExe.toPath());
            } catch (IOException e) {}
        }
    }

    public void loadFirstTime() throws IOException {
        boolean firstTime = !firstTimeFile.exists();

        if (firstTime) {
            Logger.info("first time");
            //JesusClient.INSTANCE.moduleManager.getModule(Hud.class).setToggled(true);
            //noinspection ResultOfMethodCallIgnored
            firstTimeFile.createNewFile();
            PrintWriter writer = new PrintWriter(firstTimeFile);
            writer.println("this file is for checking if this is your first time using jesus client (if it exists it's not your first time)");
            writer.close();
        }
    }

    /**
     * Convert a JSON string to pretty print version
     * @param jsonString
     * @return
     */
    public static String formatJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    public void loadScripts() {
        if (!scriptDir.exists()) scriptDir.mkdirs();

        File[] files = scriptDir.listFiles(pathname -> pathname.getName().endsWith("zip") || pathname.getName().endsWith("cbs"));

        if (files != null) {
            for (File file : files) {
                try {
                    JesusClient.INSTANCE.scriptManager.load(file);
                } catch (Exception e) {
                    Logger.error("Failed to load script " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}