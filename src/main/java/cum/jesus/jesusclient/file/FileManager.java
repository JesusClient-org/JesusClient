package cum.jesus.jesusclient.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public final class FileManager {
    private final File root;
    private final File moduleDir;
    private final File tmpDir;

    private long tmpFileNumber = System.currentTimeMillis();

    public FileManager(File root) {
        this.root = root;
        this.moduleDir = new File(root, "modules");
        this.tmpDir = new File(root, "tmp");

        if (!root.exists()) root.mkdirs();
        if (!moduleDir.exists()) moduleDir.mkdirs();
        if (!tmpDir.exists()) tmpDir.mkdirs();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        clearTmpDir();
    }

    public boolean hasFile(final String file) {
        File f = new File(root, file + ".jesus");
        return f.exists();
    }

    public JesusFile open(final String file) throws FileNotFoundException {
        File f = new File(root, file + ".jesus");

        if (!f.exists()) {
            throw new FileNotFoundException("'open' requires an existing file");
        }

        return new JesusFile(f);
    }

    public JesusFile create(final String file) throws FileAlreadyExistsException {
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

    public JesusFile get(final String file) {
        return fromFile(new File(root, file + ".jesus"));
    }

    public JesusFile fromFile(final File file) {
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

    public JesusFile getModuleFile(final String module) {
        return fromFile(new File(moduleDir, module + ".jesus"));
    }

    public JesusFile[] getAllModuleFiles() {
        File[] files = moduleDir.listFiles();
        assert files != null;

        JesusFile[] moduleFiles = new JesusFile[files.length];

        for (int i = 0; i < files.length; i++) {
            moduleFiles[i] = fromFile(files[i]);
        }

        return moduleFiles;
    }

    public File getTmpFile() {
        return getTmpFileWithName(Long.toHexString(tmpFileNumber++));
    }

    public File getNamedTmpFile(final String name) {
        return getTmpFileWithName(name + "_" + Long.toHexString(tmpFileNumber++));
    }

    private File getTmpFileWithName(final String name) {
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

    public void clearTmpDir() {
        File[] tmp = tmpDir.listFiles();
        if (tmp != null) {
            for (File file : tmp) {
                file.delete();
            }
        }
    }

    /**
     * Creates a directory and all parent directories to a java File object starting at jesusclient root
     * @param file The pathname to create in jesusclient root
     * @return true if the directory and all its parents were created, false otherwise
     */
    public boolean mkdir(final String file) {
        return new File(root, file).mkdirs();
    }
}
