package cum.jesus.jesusclient.remote;

import com.google.gson.Gson;
import com.sun.jna.platform.win32.Kernel32;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.files.FileManager;
import cum.jesus.jesusclient.utils.HttpUtils;
import cum.jesus.jesusclient.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Updater {
    public static boolean shouldUpdate = false;
    public static Update update = null;

    public static final File modDir = new File(JesusClient.INSTANCE.mc.mcDataDir + "/mods");
    public static final File updaterExe = new File(JesusClient.INSTANCE.mc.mcDataDir + "/" + JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""), "jesusupdat.exe");

    public static class Update {
        public String version;
        public String download;
        public String description;

        public Update(String version, String download, String description) {
            this.version = version;
            this.download = download;
            this.description = description;
        }
    }

    public static void loadUpdate() {
        (new Thread(() -> {
            checkForUpdate();
            if (!shouldUpdate) return;

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
        }, "Jesus-Updater")).start();
    }

    public static void checkForUpdate() {
        String url = JesusClient.backendUrl + "/api/v2/updates";
        String res = null;
        try {
            res = HttpUtils.get(url);
        } catch (Exception ignored) {}

        if (res != null) {
            update = new Gson().fromJson(res, Update.class);
            shouldUpdate = !update.version.equals(JesusClient.CLIENT_VERSION_NUMBER);
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
