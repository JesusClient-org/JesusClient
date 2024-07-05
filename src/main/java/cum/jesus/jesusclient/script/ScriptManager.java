package cum.jesus.jesusclient.script;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.injection.IndySupport;
import cum.jesus.jesusclient.script.languages.js.JavaScript;
import cum.jesus.jesusclient.script.trigger.TriggerType;
import cum.jesus.jesusclient.util.Logger;
import cum.jesus.jesusclient.util.StreamUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ScriptManager is a static class since it does asm stuff possibly before it would be instantiated in JesusClient.
 * This also means it cannot use any of JesusClients manager classes and will have to handle things like files on its own
 */
public final class ScriptManager {
    private static List<Language> languages = new ArrayList<>();
    private static List<Script> scripts = new ArrayList<>();

    static {
        languages.add(new JavaScript());
    }

    public static void setup() {
        List<Script> installedScripts = StreamUtils.distinctBy(getFoldersInDir(FileManager.scriptDir).stream().map(ScriptManager::parseScript), script -> script.getName().toLowerCase());

        scripts.addAll(installedScripts);

        loadAssetsAndJars(scripts);

        IndySupport.INSTANCE.invalidateInvocations();
    }

    public static void asmPass() {
        for (Language language : languages) {
            language.getLoader().asmSetup();
        }

        for (Language language : languages) {
            scripts.stream()
                    .filter(script -> {
                        if (script.getMetadata().asmEntry == null) {
                            return false;
                        } else {
                            for (String id : language.getLanguageIDs()) {
                                if (script.getMetadata().language.equalsIgnoreCase(id)) return true;
                            }
                            return false;
                        }
                    })
                    .forEach(script -> language.getLoader().asmPass(script, new File(script.getRoot(), script.getMetadata().asmEntry).toURI()));
        }
    }

    public static void entryPass() {
        for (Language language : languages) {
            language.getLoader().entrySetup();
        }

        for (Language language : languages) {
            scripts.stream()
                    .filter(script -> {
                        if (script.getMetadata().entry == null) {
                            return false;
                        } else {
                            for (String id : language.getLanguageIDs()) {
                                if (script.getMetadata().language.equalsIgnoreCase(id)) return true;
                            }
                            return false;
                        }
                    })
                    .forEach(script -> {
                        language.getLoader().entryPass(script, new File(script.getRoot(), script.getMetadata().entry).toURI());
                    });
        }
    }

    public static MethodHandle asmInvokeLookup(String scriptName, String functionID) {
        Script script = scripts.stream().filter(it -> it.getName().equals(scriptName)).findFirst().orElse(null);
        if (script == null)
            throw new IllegalArgumentException("No script named " + scriptName + " exists.");

        String funcPath = null;
        if (script.getMetadata().asmExposedFunctions != null)
            funcPath = script.getMetadata().asmExposedFunctions.get(functionID);

        if (funcPath == null)
            throw new IllegalArgumentException("Script " + scriptName + " contains no asm exported function with id " + functionID);

        File funcFile = new File(script.getRoot(), funcPath.replace('/', File.separatorChar).replace('\\', File.separatorChar));

        for (Language language : languages) {
            for (String id : language.getLanguageIDs()) {
                if (script.getMetadata().language.equalsIgnoreCase(id)) return language.getLoader().asmInvokeLookup(script, funcFile.toURI());
            }
        }

        throw new NoSuchElementException("No language engine supports the language " + script.getMetadata().language);
    }

    public static Script parseScript(File directory) {
        File metadataFile = new File(directory, "metadata.json");
        ScriptMetadata metadata = new ScriptMetadata();

        if (metadataFile.exists()) {
            try {
                metadata = JesusClient.gson.fromJson(FileUtils.readFileToString(metadataFile), ScriptMetadata.class);
            } catch (Exception e) {
                Logger.error("Script " + directory.getName() + " has invalid metadata.json");
            }
            }

            return new Script(directory.getName(), metadata, directory);
        }

    public static void teardown() {
        scripts.clear();

        for (Language language : languages) {
            language.getLoader().clearTriggers();
        }
    }

    public static void trigger(TriggerType type, Object[] args) {
        for (Language language : languages) {
            language.getLoader().execTriggerType(type, args);
        }
    }

    private static void loadAssetsAndJars(List<Script> scripts) {
        loadAssets(scripts);

        for (Script script : scripts) {
            if (script.getMetadata().entry != null)
                script.getMetadata().entry = script.getMetadata().entry.replace('/', File.separatorChar).replace('\\', File.separatorChar);

            if (script.getMetadata().asmEntry != null)
                script.getMetadata().asmEntry = script.getMetadata().asmEntry.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        }

        List<URL> jars = scripts.stream().flatMap(script -> {
            try (Stream<Path> paths = Files.walk(script.getRoot().toPath())) {
                List<Path> jarPaths = paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".jar"))
                        .collect(Collectors.toList());

                return jarPaths.stream().map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                return Stream.empty();
            }
        }).collect(Collectors.toList());

        for (Language language : languages) {
            language.getLoader().setup(jars);
        }
    }

    private static void loadAssets(List<Script> scripts) {
        scripts.stream()
                .map(script -> new File(script.getRoot(), "assets"))
                .filter(file -> file.exists() && file.isDirectory())
                .flatMap(file -> Arrays.stream(Objects.requireNonNull(file.listFiles())))
                .forEach(file -> {
                    try {
                        FileUtils.copyFileToDirectory(file, FileManager.root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static List<File> getFoldersInDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) return new ArrayList<>();

        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isDirectory)
                .collect(Collectors.toList());
    }
}
