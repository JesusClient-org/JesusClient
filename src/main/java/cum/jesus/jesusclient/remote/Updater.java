package cum.jesus.jesusclient.remote;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jna.platform.win32.Kernel32;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.files.FileManager;
import cum.jesus.jesusclient.gui.externalconsole.Console;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

public class Updater {
    public static boolean shouldUpdate = false;
    public static boolean shouldDLLUpdate = false;
    public static Update update = null;

    public static class Update {
        public String version;
        public String dllVersion;
        public String download;
        public String description;

        public Update(String version, String dllVersion, String download, String description) {
            this.version = version;
            this.dllVersion = dllVersion;
            this.download = download;
            this.description = description;
        }

        @Override
        public String toString() {
            return "Jar Version: " + version + "; DLL Version: " + dllVersion + "; Update Description: \"" + description + "\"";
        }
    }

    public static void loadUpdate() {
        (new Thread(() -> {
            checkForUpdate();

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (shouldUpdate) {
                int pid = Kernel32.INSTANCE.GetCurrentProcessId();
                String currentVersion = JesusClient.CLIENT_VERSION_NUMBER;
                String newVersion = update.version;
                String oldVersionJar = FileManager.srcJar.getAbsolutePath();
                Logger.debug(oldVersionJar);
                String modPath = FileManager.modDir.getAbsolutePath();
                Logger.debug(modPath);
                String newVersionDownloadUrl = update.download;
                String updateInfo = update.description != null ? update.description.replace(" ", "__") : "No description available for this update".replace(" ", "__");

                execute(FileManager.updaterExe.getAbsolutePath(), String.valueOf(pid), currentVersion, newVersion, oldVersionJar, modPath, newVersionDownloadUrl, updateInfo);
            }

            if (shouldDLLUpdate) {
                Logger.debug("dll update event :fire:");
                try {
                    File dll = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "client.dll");

                    if (!dll.exists()) throw new RuntimeException("Native DLL doesn't exist. This error should not be possible. Please ping or DM JesusTouchMe#8717 on Discord.");

                    dll.delete();

                    Files.copy(new URL(JesusClient.backendUrl + "/download/jesusclientlib").openStream(), dll.toPath());

                    // update info
                    JsonObject payload = new JsonObject();

                    payload.addProperty("jarVersion", JesusClient.CLIENT_VERSION_NUMBER);
                    payload.addProperty("dllVersion", update.dllVersion);

                    String json = FileManager.formatJson(new Gson().toJson(payload));

                    Files.write(FileManager.clientInfoFile.toPath(), json.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Updater")).start();
    }

    public static void checkForUpdate() {
        String url = JesusClient.backendUrl + "/api/v2/updates";
        String res = null;
        try {
            res = HttpUtils.get(url);
        } catch (Exception ignored) {}

        if (res != null) {
            update = new Gson().fromJson(res, Update.class);

            {
                if (!FileManager.clientInfoFile.exists()) {
                    try {
                        FileManager.clientInfoFile.createNewFile();

                        JsonObject payload = new JsonObject();

                        payload.addProperty("jarVersion", JesusClient.CLIENT_VERSION_NUMBER);
                        payload.addProperty("dllVersion", update.dllVersion);

                        String json = FileManager.formatJson(new Gson().toJson(payload));

                        Files.write(FileManager.clientInfoFile.toPath(), json.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    JsonElement infoElement = new JsonParser().parse(new String(Files.readAllBytes(FileManager.clientInfoFile.toPath())));
                    if (!infoElement.isJsonObject()) throw new RuntimeException("Client Info is not a valid JSON object.");

                    JsonObject info = infoElement.getAsJsonObject();

                    if (!info.has("dllVersion")) throw new RuntimeException("Client Info does not contain any dll version field");
                    String currentDllVersion = info.get("dllVersion").getAsString();

                    shouldDLLUpdate = !update.dllVersion.equals(currentDllVersion);

                    File dll = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "client.dll");
                    if (!dll.exists()) {
                        Files.copy(new URL(JesusClient.backendUrl + "/download/jesusclientlib").openStream(), dll.toPath());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            shouldUpdate = !update.version.equals(JesusClient.CLIENT_VERSION_NUMBER);

            Logger.info(shouldUpdate ? "Jar is outdated. Attempting auto update shortly" : "Jar is up to date");
            Logger.info(shouldDLLUpdate ? "Native DLL is outdated. Attempting update shortly" : "Native DLL is up to date");

            Logger.info(update);
        } else {
            Logger.error("An error has occurred while attempting to get update information");
        }
    }

    public static int execute(String path, String pid, String currentVersion, String newVersion, String oldVersionJar, String modDir, String newVersionDownloadUrl, String updateInfo) {
        AtomicInteger exitCode = new AtomicInteger();
        (new Thread(() -> {
            try {
                // specify the command to run the EXE application
                String command = path;

                // create a new ProcessBuilder object with the command and arguments
                ProcessBuilder pb = new ProcessBuilder(command, pid, currentVersion, newVersion, oldVersionJar, modDir, newVersionDownloadUrl, updateInfo);

                // start the process and wait for it to finish
                Process process = pb.start();
                process.waitFor();

                // get the exit code of the process
                exitCode.set(process.exitValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Jesus-Screen")).start();
        return exitCode.get();
    }
}
