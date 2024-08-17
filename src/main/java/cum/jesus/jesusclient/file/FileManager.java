package cum.jesus.jesusclient.file;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.util.ChatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.stream.Collectors;

public final class FileManager {
    public static final File root;
    public static final File assetsDir;
    public static final File configDir;
    public static final File resourcesDir;
    public static final File scriptDir;
    public static final File tmpDir;

    static {
        root = new File(JesusClient.mc == null ? new File(".") : JesusClient.mc.mcDataDir == null ? new File(".") : JesusClient.mc.mcDataDir, "jesusclient");
        assetsDir = new File(root, "assets");
        configDir = new File(root, "config");
        resourcesDir = new File(root, "resources");
        scriptDir = new File(root, "scripts");
        tmpDir = new File(root, "tmp");

        root.mkdirs();
        assetsDir.mkdirs();
        configDir.mkdirs();
        resourcesDir.mkdirs();
        scriptDir.mkdirs();
        tmpDir.mkdirs();
    }

    private static File get(File dir, String name) {
        File file = new File(dir, name);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return file;
    }

    public static boolean hasFile(String file) {
        File f = new File(root, file + ".jesus");
        return f.exists();
    }

    public static File get(String fileName) {
        return get(root, fileName);
    }

    public static File getConfigFile(String name) {
        File file = new File(configDir, name + ".jesus");
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        return file;
    }

    public static String read(File file) {
        if (!file.exists()) return null;

        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            e.printStackTrace();
            ChatUtils.sendPrefixMessage("IO exception occurred when attempting to read file at " + file.getAbsolutePath());
            return null;
        }
    }

    public static void write(File file, String string) {
        try {
            if (!file.exists()) file.createNewFile();

            FileUtils.write(file, string);
        } catch (IOException e) {
            e.printStackTrace();
            ChatUtils.sendMessage("IO exception occurred when attempting to write to file at " + file.getAbsolutePath());
        }
    }

    public static String saveResourceString(String resourceName, String outputName, boolean replace) {
        if (resourceName == null || resourceName.isEmpty() || outputName == null || outputName.isEmpty()) {
            return null;
        }

        File output = new File(resourcesDir, outputName);

        if (output.exists() && !replace) {
            return read(output);
        }

        resourceName = resourceName.replace('\\', '/');
        try (InputStream resource = FileManager.class.getResourceAsStream(resourceName)) {
            if (resource == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourceName + "' cannot be found");
            }

            String res = new BufferedReader(new InputStreamReader(resource)).lines().collect(Collectors.joining(System.lineSeparator()));
            write(output, res);
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File saveResource(String resourceName, String outputName, boolean replace) {
        if (resourceName == null || resourceName.isEmpty() || outputName == null || outputName.isEmpty()) {
            return null;
        }

        File output = new File(resourcesDir, outputName);

        if (output.exists() && !replace) {
            return output;
        }

        try (InputStream resource = FileManager.class.getResourceAsStream(resourceName)) {
            if (resource == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourceName + "' cannot be found");
            }

            FileUtils.copyInputStreamToFile(resource, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    public static void loadLibraryFromResources(String libName) {
        if (libName == null || libName.isEmpty()) {
            return;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            libName += ".dll";
        } else if (SystemUtils.IS_OS_LINUX) {
            libName += ".so";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        String[] parts = libName.replace('\\', File.separatorChar).replace('/', File.separatorChar).split("/");
        String fileName = parts[parts.length - 1];
        File output = saveResource(libName, fileName, true);

        System.load(output.getAbsolutePath());
    }

    public static File getTmpFile() {
        return getTmpFileWithName(Long.toHexString(System.currentTimeMillis()));
    }

    public static File getNamedTmpFile(final String name) {
        return getTmpFileWithName(name + "_" + Long.toHexString(System.currentTimeMillis()));
    }

    private static File getTmpFileWithName(final String name) {
        File file = new File(tmpDir, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public static void clearTmpDir() {
        File[] tmp = tmpDir.listFiles();
        if (tmp != null) {
            for (File file : tmp) {
                file.delete();
            }
        }
    }
}
