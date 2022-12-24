package cum.jesus.jesusclient.scripting;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.scripting.runtime.ScriptRuntime;
import cum.jesus.jesusclient.utils.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ScriptManager {
    private static final String scriptHeader = "var runtime = Java.type('" + ScriptRuntime.class.getCanonicalName() + "');\n" +
            "var mc = runtime.getMinecraft();\n" +
            "var JesusClient = runtime.getJesusClient();\n" +
            "var Logger = JesusClient.getLogger();\n" +
            "var ChatUtils = JesusClient.getChatUtils();\n" +
            "var HttpUtils = JesusClient.getHttpUtils();\n" +
            "";
    private ScriptEngine engine;

    public ScriptManager() {
        newScript();
    }

    public void newScript() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");

        if (engine == null) return;

        try {
            engine.eval(scriptHeader);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object eval(String script) throws ScriptException {
        if (engine == null) return "Failed to initialize engine";

        return engine.eval(script);
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

        content = scriptHeader + content;

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            scriptEngine.eval(content.replace("const","var").replace("let","var"));
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

        content = scriptHeader + content;

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            scriptEngine.eval(content);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        command.setScriptEngine(scriptEngine);

        return command;
    }

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
        indexContent = scriptHeader + indexContent;

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            scriptEngine.eval(indexContent);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        index.setScriptEngine(scriptEngine);

        return index;
    }
}
