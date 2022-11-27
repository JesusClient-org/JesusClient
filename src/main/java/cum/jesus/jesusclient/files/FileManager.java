package cum.jesus.jesusclient.files;

import com.google.common.io.Files;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileManager {
    private final File clientDir = new File(JesusClient.INSTANCE.mc.mcDataDir, JesusClient.CLIENT_NAME.toLowerCase().replace(" ", ""));
    public final File backupDir = new File(clientDir, "config-backups");
    public final File cacheDir = new File(clientDir, "CACHE");
    public final File configFile = new File(clientDir, "jesusconfig.json");
    private final File firstTimeFile = new File(clientDir, "firsttime.txt");

    public void save() throws Exception {
        if (!configFile.exists() && !configFile.createNewFile())
            throw new IOException("Failed to create config file");

        Files.write(JesusClient.INSTANCE.configManager.toJsonObject().toString().getBytes(StandardCharsets.UTF_8), configFile);
    }

    public void init() {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        backupDir.mkdirs();

        // delete oldest backup if there's more than 25
        File[] backups = backupDir.listFiles();
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;
        if( backups != null && backups.length >= 25) {
            //delete oldest files after theres more than 25 backup files
            for(File f : backups) {
                if(f.lastModified() < oldestDate) {
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }

            if(oldestFile != null) {
                Logger.info("Deleted config backup: " + oldestFile.getName());
                //noinspection ResultOfMethodCallIgnored
                oldestFile.delete();
            }
        }
    }

    public void loadFirstTime() throws IOException {
        boolean firstTime = !firstTimeFile.exists();

        if (firstTime) {
            Logger.info("first time");

            //noinspection ResultOfMethodCallIgnored
            firstTimeFile.createNewFile();
            PrintWriter writer = new PrintWriter(firstTimeFile);
            writer.println("this file is for checking if this is your first time using jesus client (if it exists it's not your first time)");
            writer.close();
        }
    }
}
