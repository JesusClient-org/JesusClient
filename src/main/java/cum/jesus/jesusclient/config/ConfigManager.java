package cum.jesus.jesusclient.config;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.files.JesusEncoding;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.Setting;
import cum.jesus.jesusclient.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigManager {
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();

        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", JesusClient.CLIENT_VERSION_NUMBER);

            obj.add("metadata", metadata);
        }

        {
            JsonObject modules = new JsonObject();

            for (Module module : JesusClient.INSTANCE.moduleManager.getModules()) {
                JsonObject moduleObject = new JsonObject();

                moduleObject.addProperty("toggled", module.isToggled());
                moduleObject.addProperty("keybind", module.getKeybind());

                modules.add(module.getName(), moduleObject);
            }

            obj.add("modules", modules);
        }
        {
            JsonObject values = new JsonObject();

            for (Map.Entry<String, List<Setting>> stringListEntry : JesusClient.INSTANCE.settingManager.getAllValues().entrySet()) {
                JsonObject value = new JsonObject();

                for (Setting value1 : stringListEntry.getValue()) value1.addToJsonObject(value);

                values.add(stringListEntry.getKey(), value);
            }

            obj.add("settings", values);
        }

        return obj;
    }

    public void load() {
        if (!JesusClient.INSTANCE.fileManager.configFile.exists()) return;

        List<String> backupReasons = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(JesusClient.INSTANCE.fileManager.configFile))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonObject object = (JsonObject) new JsonParser().parse(JesusEncoding.fromString(sb.toString()));

        //<editor-fold desc="metadata">
        if (object.has("metadata")) {
            JsonElement metadataElement = object.get("metadata");

            if (metadataElement instanceof JsonObject) {
                JsonObject metadata = (JsonObject) metadataElement;

                JsonElement clientVersion = metadata.get("clientVersion");

                if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isString()) {
                    String version = clientVersion.getAsString();

                    if (!version.equals(JesusClient.CLIENT_VERSION_NUMBER)) {
                        metadata.addProperty("clientVersion", JesusClient.CLIENT_VERSION_NUMBER);
                    }
                } else {
                    backupReasons.add("'clientVersion' object is not valid.");
                }
            } else {
                backupReasons.add("'metadata' object is not valid.");
            }

        } else {
            backupReasons.add("Config file has no metadata");
        }
        //</editor-fold>

        //<editor-fold desc="modules">
        JsonElement modulesElement = object.get("modules");

        if (modulesElement instanceof JsonObject) {
            JsonObject modules = (JsonObject) modulesElement;

            for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                Module module = JesusClient.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), true);

                if (module == null) {
                    backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                    continue;
                }

                if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                    JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                    JsonElement state = moduleObject.get("toggled");

                    if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                        module.setToggledNoNotif(state.getAsBoolean());
                    } else {
                        backupReasons.add("'" + stringJsonElementEntry.getKey() + "/toggled' isn't valid");
                    }

                    JsonElement keybind = moduleObject.get("keybind");

                    if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {
                        module.setKeybind(keybind.getAsInt());
                    } else {
                        backupReasons.add("'" + stringJsonElementEntry.getKey() + "/keybind' isn't valid");
                    }
                } else {
                    backupReasons.add("Module object '" + stringJsonElementEntry.getKey() + "' isn't valid");
                }
            }
        } else {
            backupReasons.add("'modules' object is not valid");
        }
        //</editor-fold>

        //<editor-fold desc="values">
        JsonElement valuesElement = object.get("settings");

        if (valuesElement instanceof JsonObject) {
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                List<Setting> values = JesusClient.INSTANCE.settingManager.getAllSettingsFrom(stringJsonElementEntry.getKey());

                if (values == null) {
                    backupReasons.add("Value owner '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                    continue;
                }

                if (!stringJsonElementEntry.getValue().isJsonObject()) {
                    backupReasons.add("'values/" + stringJsonElementEntry.getKey() + "' is not valid");
                    continue;
                }

                JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                for (Setting value : values) {
                    try {
                        if (value.isPremiumOnly() && !Premium.isUserPremium()) return;
                        value.fromJsonObject(valueObject);
                    } catch (Exception e) {
                        backupReasons.add("Error while applying 'values/" + stringJsonElementEntry.getKey() + "' " + e.toString());
                    }
                }
            }
        } else {
            backupReasons.add("'values' is not valid");
        }

        if (backupReasons.size() > 0) {
            backup(backupReasons);
        }
    }

    private void backup(@NotNull List<String> backupReasons) {
        Logger.info("Creating backup " + backupReasons);

        try {
            File out = new File(JesusClient.INSTANCE.fileManager.backupDir, "backup_" + System.currentTimeMillis() + ".zip");
            out.createNewFile();

            StringBuilder reason = new StringBuilder();

            for (String backupReason : backupReasons) {
                reason.append("- ").append(backupReason).append("\n");
            }

            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(out));

            outputStream.putNextEntry(new ZipEntry("jesusconfig.json"));
            Files.copy(JesusClient.INSTANCE.fileManager.configFile, outputStream);
            outputStream.closeEntry();

            outputStream.putNextEntry(new ZipEntry("reason.txt"));
            outputStream.write(reason.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.closeEntry();

            outputStream.close();
        } catch (Exception e) {
            Logger.error("Failed to backup");
            e.printStackTrace();
        }

    }
}
