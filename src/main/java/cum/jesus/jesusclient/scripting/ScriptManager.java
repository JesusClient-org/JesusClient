package cum.jesus.jesusclient.scripting;

import com.google.common.io.ByteStreams;
import com.google.gson.*;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.clickgui.BoringRenderThingy;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.module.settings.ModeSetting;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.scripting.runtime.deobfedutils.*;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.font.GlyphPageFontRenderer;
import jdk.internal.dynalink.beans.StaticClass;
import me.superblaubeere27.clickgui.IRenderer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.function.Function;


public class ScriptManager {
    private ScriptEngine scriptEngine;

    public ScriptManager() {
        newScript();
    }

    public void newScript() {
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        if (scriptEngine == null) {
            Logger.error("scriptengine is null (wtf)");
            return;
        }

        GlyphPageFontRenderer consolas = GlyphPageFontRenderer.create("Consolas", 15, false, false, false);
        IRenderer renderer = new BoringRenderThingy(consolas);

        // global instances
        scriptEngine.put("mc", JesusClient.mc);
        scriptEngine.put("moduleManager", JesusClient.INSTANCE.moduleManager);
        scriptEngine.put("basicFont", consolas);
        scriptEngine.put("basicRenderer", renderer);

        // global classes
        scriptEngine.put("Logger", StaticClass.forClass(ScriptLogger.class));
        scriptEngine.put("ChatUtils", StaticClass.forClass(ScriptChatUtils.class));
        scriptEngine.put("DesktopUtils", StaticClass.forClass(ScriptDesktopUtils.class));
        scriptEngine.put("HttpUtils", StaticClass.forClass(ScriptHttpUtils.class));
        scriptEngine.put("RenderUtils", StaticClass.forClass(ScriptRenderUtils.class));

        // global functions
        scriptEngine.put("setDevMode", new SetDevMode());

        try {
            scriptEngine.eval("Logger.info('Loaded new Script Engine');");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object eval(String script) throws ScriptException {
        if (scriptEngine == null) return "Failed to initialize engine";

        return scriptEngine.eval(script);
    }

    public Script load(File scriptFile) {
        try {
            ZipFile zipFile = new ZipFile(scriptFile);

            ZipEntry manifestEntry = zipFile.getEntry("manifest.json");
            if (manifestEntry == null) throw new RuntimeException("No manifest file was found in " + scriptFile.getName() + " ('manifest.json')");

            JsonElement manifestElement = new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(manifestEntry)));
            if (!manifestElement.isJsonObject()) throw new RuntimeException("Manifest is not a valid Json object");

            JsonObject manifest = manifestElement.getAsJsonObject();

            String scriptName;
            String scriptDesc;
            String scriptVer;
            String[] scriptAuthors;
            String scriptIndex;

            //<editor-fold desc="Metadata">
            {
                if (!manifest.has("name")) throw new RuntimeException("Manifest does not contain 'name'");
                JsonElement element = manifest.get("name");

                if (element.isJsonPrimitive()) scriptName = element.getAsString();
                else throw new RuntimeException("'name' is not valid");

                if (!manifest.has("description")) scriptDesc = "No description found";
                else {
                    element = manifest.get("description");

                    if (element.isJsonPrimitive()) scriptDesc = element.getAsString();
                    else throw new RuntimeException("'description' is not valid");
                }

                if (!manifest.has("version")) throw new RuntimeException("Manifest does not contain 'version'");
                element = manifest.get("version");

                if (element.isJsonPrimitive()) scriptVer = element.getAsString();
                else throw new RuntimeException("'version' is not valid");

                if (!manifest.has("authors")) throw new RuntimeException("Manifest does not contain 'authors'");
                element = manifest.get("authors");

                if (element.isJsonArray()) scriptAuthors = new Gson().fromJson(element.getAsJsonArray(), String[].class);
                else throw new RuntimeException("'authors' is not valid");

                if (!manifest.has("index")) throw new RuntimeException("Manifest does not contain 'index'");
                element = manifest.get("index");

                if (element.isJsonPrimitive()) scriptIndex = element.getAsString();
                else throw new RuntimeException("'index' is not valid");
            }
            //</editor-fold>

            Script script = new Script(scriptName, scriptDesc, scriptVer, scriptAuthors, loadIndex(scriptIndex, zipFile));

            if (manifest.has("modules")) {
                JsonElement element = manifest.get("modules");

                if (!element.isJsonArray()) throw new RuntimeException("'modules' has to be an array");

                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    ScriptModule module = loadModule(jsonElement, zipFile, scriptName);
                    script.getModules().add(module);
                }
            }

            if (manifest.has("commands")) {
                JsonElement element = manifest.get("commands");

                if (!element.isJsonArray()) throw new RuntimeException("'commands' has to be an array");

                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    ScriptCommand command = loadCommand(jsonElement, zipFile, scriptName);
                    script.getCommands().add(command);
                }
            }

            script.register();

            Logger.info("Successfully loaded " + script.getName() + " " + script.getVersion());

            return script;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open Zip file", ex);
        }
    }

    private ScriptModule loadModule(JsonElement jsonElement, ZipFile file, String scriptName) {
        if (!jsonElement.isJsonObject()) throw new RuntimeException("A module has to be a Json object");

        JsonObject obj = jsonElement.getAsJsonObject();

        String name;
        String desc;
        String cat;
        String indexFile;

        //<editor-fold desc="Metadata">
        {
            if (!obj.has("name")) throw new RuntimeException("Module does not contain 'name'");
            JsonElement element = obj.get("name");

            if (element.isJsonPrimitive()) name = element.getAsString();
            else throw new RuntimeException("'name' is invalid");
        }
        {
            if (!obj.has("description")) throw new RuntimeException("No 'description' was specified in '" + name + "'");
            JsonElement element = obj.get("description");

            if (element.isJsonPrimitive()) desc = element.getAsString();
            else throw new RuntimeException("'description' is invalid");
        }
        {
            if (!obj.has("category")) throw new RuntimeException("No 'category' was specified in '" + name + "'");
            JsonElement element = obj.get("category");

            if (element.isJsonPrimitive()) cat = element.getAsString();
            else throw new RuntimeException("'category' is invalid");
        }
        {
            if (!obj.has("index")) throw new RuntimeException("No 'index' was specified in '" + name + "'");
            JsonElement element = obj.get("index");

            if (element.isJsonPrimitive()) indexFile = element.getAsString();
            else throw new RuntimeException("'index' is invalid");
        }
        //</editor-fold>

        Category category;

        try {
            category = Category.valueOf(cat.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("There's no category called '" + cat + "'. Allowed categories: " + Arrays.toString(Category.values()));
        }

        ScriptModule module = new ScriptModule("(" + scriptName + ") " + name, desc, category);

        JesusClient.INSTANCE.settingManager.registerObject(module.getName(), module);

        // Settings
        {
            if (obj.has("settings")) {
                if (!obj.get("settings").isJsonArray()) throw new RuntimeException("'settings' is invalid");

                JsonArray settings = obj.get("settings").getAsJsonArray();

                for (JsonElement element : settings) {
                    if (element.isJsonObject()) {
                        JsonObject object = element.getAsJsonObject();

                        String type;

                        {
                            if (!object.has("type")) throw new RuntimeException("No 'type' was specified in" + name + "/settings");
                            JsonElement e = object.get("type");

                            if (e.isJsonPrimitive()) type = e.getAsString();
                            else throw new RuntimeException("'type' is invalid");
                        }

                        switch (type) {
                            case "boolean":
                                String booleanName;
                                boolean booleanDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) booleanName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) booleanDefault = e.getAsBoolean();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(new BooleanSetting(booleanName, booleanDefault, true));

                                break;
                            case "mode":
                                String modeName;
                                String[] modeValues;
                                String modeDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) modeName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("values")) throw new RuntimeException("No 'values' was specified in" + name + "/settings");
                                    JsonElement e = object.get("values");

                                    if (e.isJsonArray()) modeValues = new Gson().fromJson(e.getAsJsonArray(), String[].class);
                                    else throw new RuntimeException("'values' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) modeDefault = e.getAsString();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(new ModeSetting(modeName, modeDefault, true, modeValues));

                                break;
                            case "int":
                                String intName;
                                int intMax;
                                int intMin;
                                int intDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) intName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("min")) throw new RuntimeException("No 'min' was specified in" + name + "/settings");
                                    JsonElement e = object.get("min");

                                    if (e.isJsonPrimitive()) intMin = e.getAsInt();
                                    else throw new RuntimeException("'min' is invalid");
                                }
                                {
                                    if (!object.has("max")) throw new RuntimeException("No 'max' was specified in" + name + "/settings");
                                    JsonElement e = object.get("max");

                                    if (e.isJsonPrimitive()) intMax = e.getAsInt();
                                    else throw new RuntimeException("'max' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) intDefault = e.getAsInt();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                NumberSetting<Integer> integerNumberSetting = new NumberSetting<>(intName, intDefault, intMin, intMax, true);

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(integerNumberSetting);

                                break;
                            case "long":
                                String longName;
                                long longMin;
                                long longMax;
                                long longDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) longName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("min")) throw new RuntimeException("No 'min' was specified in" + name + "/settings");
                                    JsonElement e = object.get("min");

                                    if (e.isJsonPrimitive()) longMin = e.getAsLong();
                                    else throw new RuntimeException("'min' is invalid");
                                }
                                {
                                    if (!object.has("max")) throw new RuntimeException("No 'max' was specified in" + name + "/settings");
                                    JsonElement e = object.get("max");

                                    if (e.isJsonPrimitive()) longMax = e.getAsLong();
                                    else throw new RuntimeException("'max' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) longDefault = e.getAsLong();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                NumberSetting<Long> longNumberSetting = new NumberSetting<>(longName, longDefault, longMin, longMax, true);

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(longNumberSetting);

                                break;
                            case "float":
                                String floatName;
                                float floatMin;
                                float floatMax;
                                float floatDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) floatName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("min")) throw new RuntimeException("No 'min' was specified in" + name + "/settings");
                                    JsonElement e = object.get("min");

                                    if (e.isJsonPrimitive()) floatMin = e.getAsFloat();
                                    else throw new RuntimeException("'min' is invalid");
                                }
                                {
                                    if (!object.has("max")) throw new RuntimeException("No 'max' was specified in" + name + "/settings");
                                    JsonElement e = object.get("max");

                                    if (e.isJsonPrimitive()) floatMax = e.getAsFloat();
                                    else throw new RuntimeException("'max' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) floatDefault = e.getAsFloat();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                NumberSetting<Float> floatNumberSetting = new NumberSetting<>(floatName, floatDefault, floatMin, floatMax, true);

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(floatNumberSetting);

                                break;
                            case "double":
                                String doubleName;
                                double doubleMin;
                                double doubleMax;
                                double doubleDefault;

                                {
                                    if (!object.has("name")) throw new RuntimeException("No 'name' was specified in" + name + "/settings");
                                    JsonElement e = object.get("name");

                                    if (e.isJsonPrimitive()) doubleName = e.getAsString();
                                    else throw new RuntimeException("'name' is invalid");
                                }
                                {
                                    if (!object.has("min")) throw new RuntimeException("No 'min' was specified in" + name + "/settings");
                                    JsonElement e = object.get("min");

                                    if (e.isJsonPrimitive()) doubleMin = e.getAsDouble();
                                    else throw new RuntimeException("'min' is invalid");
                                }
                                {
                                    if (!object.has("max")) throw new RuntimeException("No 'max' was specified in" + name + "/settings");
                                    JsonElement e = object.get("max");

                                    if (e.isJsonPrimitive()) doubleMax = e.getAsDouble();
                                    else throw new RuntimeException("'max' is invalid");
                                }
                                {
                                    if (!object.has("default")) throw new RuntimeException("No 'default' was specified in" + name + "/settings");
                                    JsonElement e = object.get("default");

                                    if (e.isJsonPrimitive()) doubleDefault = e.getAsDouble();
                                    else throw new RuntimeException("'default' is invalid");
                                }

                                NumberSetting<Double> doubleNumberSetting = new NumberSetting<>(doubleName, doubleDefault, doubleMin, doubleMax, true);

                                JesusClient.INSTANCE.settingManager.getAllSettingsFrom(module.getName()).add(doubleNumberSetting);

                                break;
                        }
                    }
                }
            }
        }

        ZipEntry entry = file.getEntry(indexFile);

        if (entry == null) {
            throw new RuntimeException("Script file doesn't contain '" + indexFile + "'");
        }

        String content;

        try {
            content = new String(ByteStreams.toByteArray(file.getInputStream(entry)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (content.contains("cum.jesus.jesusclient")) throw new RuntimeException("Attempted use of 'cum.jesus.jesusclient' Java package");
        if (content.contains("getSessionID") || content.contains("getToken")) throw new RuntimeException("Fuck ratters");

        try {
            scriptEngine.eval(content);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        module.setScriptEngine(scriptEngine);

        return module;
    }

    private ScriptCommand loadCommand(JsonElement jsonElement, ZipFile file, String scriptName) {
        if (!jsonElement.isJsonObject()) throw new RuntimeException("a command has to be a Json object");

        JsonObject obj = jsonElement.getAsJsonObject();

        String name;
        String desc;
        String[] alias;
        String indexFile;

        //<editor-fold desc="Metadata">
        {
            if (!obj.has("name")) throw new RuntimeException("Module does not contain 'name'");
            JsonElement element = obj.get("name");

            if (element.isJsonPrimitive()) name = element.getAsString();
            else throw new RuntimeException("'name' is invalid");
        }
        {
            if (!obj.has("description")) throw new RuntimeException("No 'description' was specified in '" + name + "'");
            JsonElement element = obj.get("description");

            if (element.isJsonPrimitive()) desc = element.getAsString();
            else throw new RuntimeException("'description' is invalid");
        }
        {
            if (!obj.has("alias")) alias = new String[0];
            else {
                JsonElement element = obj.get("alias");

                if (element.isJsonArray()) alias = new Gson().fromJson(element.getAsJsonArray(), String[].class);
                else throw new RuntimeException("'alias' is not valid");
            }
        }
        {
            if (!obj.has("index")) throw new RuntimeException("No 'index' was specified in '" + name + "'");
            JsonElement element = obj.get("index");

            if (element.isJsonPrimitive()) indexFile = element.getAsString();
            else throw new RuntimeException("'index' is invalid");
        }
        //</editor-fold>

        ScriptCommand command = new ScriptCommand(name, desc, alias);
        ZipEntry entry = file.getEntry(indexFile);

        if (entry == null) {
            throw new RuntimeException("Script file doesn't contain '" + indexFile + "'");
        }

        String content;

        try {
            content = new String(ByteStreams.toByteArray(file.getInputStream(entry)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (content.contains("cum.jesus.jesusclient")) throw new RuntimeException("Attempted use of 'cum.jesus.jesusclient' Java package");
        if (content.contains("getSessionID") || content.contains("getToken")) throw new RuntimeException("Fuck ratters");

        try {
            scriptEngine.eval(content);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        command.setScriptEngine(scriptEngine);

        return command;
    }

    //todo: make it do more
    private ScriptIndex loadIndex(String indexFile, ZipFile file) {
        ScriptIndex index = new ScriptIndex();
        ZipEntry entry = file.getEntry(indexFile);

        if (entry == null)
            throw new RuntimeException("There's no " + indexFile + " in the script");

        String indexContent;

        try {
            indexContent = new String(ByteStreams.toByteArray(file.getInputStream(entry)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (indexContent.contains("cum.jesus.jesusclient")) throw new RuntimeException("Attempted use of 'cum.jesus.jesusclient' Java package");
        if (indexContent.contains("getSessionID") || indexContent.contains("getToken")) throw new RuntimeException("Fuck ratters");

        try {
            scriptEngine.eval(indexContent);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        index.setScriptEngine(scriptEngine);

        return index;
    }

    private class SetDevMode implements Consumer<Boolean> {
        @Override
        public void accept(Boolean b) {
            JesusClient.devMode = b;
        }
    }
}
