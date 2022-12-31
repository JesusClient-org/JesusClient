package cum.jesus.jesusclient.scripting;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.clickgui.BoringRenderThingy;
import cum.jesus.jesusclient.module.Category;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ScriptManager {
    private ScriptEngine scriptEngine;

    public ScriptManager() {
        newScript();
    }

    public void newScript() {
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

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


        if (scriptEngine == null) return;

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

            Script script = new Script(scriptName, scriptDesc, scriptVer, loadIndex(scriptIndex, zipFile));

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
        String[] settings;
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
}
