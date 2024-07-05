package cum.jesus.jesusclient.file;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.util.ChatUtils;
import org.apache.commons.io.FileUtils;
import org.lwjgl.Sys;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileManager {
    public static final File root;
    public static final File assetsDir;
    public static final File moduleDir;
    public static final File resourcesDir;
    public static final File scriptDir;
    public static final File tmpDir;

    static {
        root = new File(JesusClient.mc == null ? new File(".") : JesusClient.mc.mcDataDir == null ? new File(".") : JesusClient.mc.mcDataDir, "jesusclient");
        assetsDir = new File(root, "assets");
        moduleDir = new File(root, "modules");
        resourcesDir = new File(root, "resources");
        scriptDir = new File(root, "scripts");
        tmpDir = new File(root, "tmp");

        root.mkdirs();
        assetsDir.mkdirs();
        moduleDir.mkdirs();
        resourcesDir.mkdirs();
        scriptDir.mkdirs();
        tmpDir.mkdirs();
    }

    public static boolean hasFile(final String file) {
        File f = new File(root, file + ".jesus");
        return f.exists();
    }

    public static JesusFile open(final String file) throws FileNotFoundException {
        File f = new File(root, file + ".jesus");

        if (!f.exists()) {
            throw new FileNotFoundException("'open' requires an existing file");
        }

        return new JesusFile(f);
    }

    public static JesusFile create(final String file) throws FileAlreadyExistsException {
        File f = new File(root, file + ".jesus");

        if (f.exists()) {
            throw new FileAlreadyExistsException("'create' requires a file to not exist");
        }

        try {
            f.createNewFile();
        } catch (IOException e) {
            return null;
        }

        return new JesusFile(f);
    }

    public static JesusFile get(final String file) {
        return fromFile(new File(root, file + ".jesus"));
    }

    public static JesusFile fromFile(final File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return new JesusFile(file);
    }

    public static JesusFile getModuleFile(final String module) {
        return fromFile(new File(moduleDir, module + ".jesus"));
    }

    public static JesusFile[] getAllModuleFiles() {
        File[] files = moduleDir.listFiles();
        assert files != null;

        JesusFile[] moduleFiles = new JesusFile[files.length];

        for (int i = 0; i < files.length; i++) {
            moduleFiles[i] = fromFile(files[i]);
        }

        return moduleFiles;
    }

    // normal file stuff

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

    public static String saveResource(String resourceName, String outputName, boolean replace) {
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
