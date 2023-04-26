package cum.jesus.jesusclient.files;

import com.google.common.io.Files;
import com.google.gson.*;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.scripting.LibraryScript;
import cum.jesus.jesusclient.scripting.Script;
import cum.jesus.jesusclient.utils.Logger;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {
    public final File clientDir = new File(JesusClient.mc.mcDataDir, JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""));
    public final File backupDir = new File(clientDir, "config-backups");
    public final File cacheDir = new File(clientDir, "CACHE");
    public final File tmpDir = new File(cacheDir, "tmp");
    public final File scriptDir = new File(clientDir, "scripts");

    public final File configFile = new File(clientDir, "config.jesus");
    public static final File clientInfoFile = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "client.json");
    private final File firstTimeFile = new File(clientDir, "firsttime.jesus");

    public static final File modDir = new File(JesusClient.mc.mcDataDir + "/mods");

    // externals
    public static File updaterExe = new File(JesusClient.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "up.exe");
    public File map = new File(cacheDir, "map.bin");

    public static File srcJar = null;

    public void save() throws Exception {
        if (!configFile.exists() && !configFile.createNewFile())
            throw new IOException("Failed to create config file");

        Logger.info("Saving config...");

        Files.write(JesusEncoding.toString(formatJson(JesusClient.INSTANCE.configManager.toJsonObject().toString())).getBytes(StandardCharsets.UTF_8), configFile);

        if (JesusClient.devMode) {
            File readableConfig = new File(clientDir, "config_dev.json");

            Files.write(formatJson(JesusClient.INSTANCE.configManager.toJsonObject().toString()).getBytes(StandardCharsets.UTF_8), readableConfig);
        }
    }

    public void loadDll() {
        File dll = new File(clientDir, "client.dll");

        System.load(dll.getAbsolutePath());
    }

    public void init() {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        backupDir.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        tmpDir.mkdirs();

        // clear tmp dir
        File[] tmp = tmpDir.listFiles();
        if (tmp != null) {
            for (File f : tmp) {
                f.delete();
            }
        }

        try {
            if (!map.exists()) map.createNewFile();
            JesusEncoding.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // delete the oldest backup if there's more than 25
        {
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

        loadDll();
    }

    public static void doUpdater() {
        srcJar = JesusClient.devMode ? new File("C:/jesus client/Mixin Injection Client") :  new File(Objects.requireNonNull(getPathJar()));
        Logger.debug(srcJar.getAbsolutePath());

        if (!updaterExe.exists()) {
            try {
                java.nio.file.Files.copy(new URL(JesusClient.backendUrl + "/download/updater").openStream(), updaterExe.toPath());
            } catch (IOException e) {}
        }
    }

    private static String getPathJar() {
        try {
            final URI jarUriPath =
                    JesusClient.class.getResource(JesusClient.class.getSimpleName() + ".class").toURI();
            String jarStringPath = jarUriPath.toString().replace("jar:", "");
            String jarCleanPath  = Paths.get(new URI(jarStringPath)).toString();

            if (jarCleanPath.toLowerCase().contains(".jar")) {
                return jarCleanPath.substring(0, jarCleanPath.lastIndexOf(".jar") + 4);
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.error("Error getting JAR path.", e);
            return null;
        }
    }

    public void loadFirstTime() throws IOException {
        boolean firstTime = !firstTimeFile.exists();

        if (firstTime) {
            Logger.info("first time");
            //JesusClient.INSTANCE.moduleManager.getModule(Hud.class).setToggled(true);
            //noinspection ResultOfMethodCallIgnored
            firstTimeFile.createNewFile();
            Files.write(JesusEncoding.toString("this file will just indicate that you are not using jesus client for the first time").getBytes(StandardCharsets.UTF_8), firstTimeFile);
        }
    }

    /**
     * Convert a JSON string to pretty print version
     * @param jsonString
     * @return A readable Json string
     */
    public static String formatJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    public void loadScripts() {
        if (!Premium.isUserPremium()) return;

        if (!scriptDir.exists()) scriptDir.mkdirs();

        File[] files = scriptDir.listFiles(pathname -> pathname.getName().endsWith("zip") || pathname.getName().endsWith("cbs"));
        File[] singleFiles = scriptDir.listFiles(pathname -> pathname.getName().endsWith("js"));

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

        if (singleFiles != null) {
            for (File file : singleFiles) {
                try {
                    JesusClient.INSTANCE.scriptManager.loadOneFile(file);
                } catch (Exception e) {
                    Logger.error("Failed to load script " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        List<Script> tmpScripts = new ArrayList<>(JesusClient.INSTANCE.scriptManager.getScripts());
        tmpScripts.addAll(JesusClient.INSTANCE.scriptManager.getLibs());
        for (Script script : tmpScripts) {
            for (String string : script.getDependencies()) {
                if (JesusClient.INSTANCE.scriptManager.getLibNames().contains(string)) {
                    Logger.debug("requirement for " + script.getName() + ": " + string + " found!");

                    LibraryScript lib = JesusClient.INSTANCE.scriptManager.getLibraryByName(string);

                    if (lib == null) {
                        JesusClient.INSTANCE.scriptManager.getScripts().remove(script);
                        tmpScripts.remove(script);

                        script.purge();

                        Logger.error("Requirement for " + script.getName() + ": " + string + " was found, but not returned correctly.");
                        continue;
                    }

                    try {
                        lib.addObjectToEngine(script.getIndex().getEngine());
                    } catch (ScriptException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    JesusClient.INSTANCE.scriptManager.getScripts().remove(script);
                    tmpScripts.remove(script);

                    script.purge();

                    Logger.error("Requirement for " + script.getName() + ": " + string + " was not found.");
                }
            }
        }
    }
}