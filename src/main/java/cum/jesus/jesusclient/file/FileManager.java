package cum.jesus.jesusclient.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public final class FileManager {
    private final File root;

    public FileManager(File root) {
        this.root = root;

        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public JesusFile open(final String file) throws FileNotFoundException {
        File f = new File(root, file);

        if (!f.exists()) {
            throw new FileNotFoundException("'open' requires an existing file");
        }

        return new JesusFile(f);
    }

    public JesusFile create(final String file) throws FileAlreadyExistsException {
        File f = new File(root, file);

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
        File f = new File(root, file);

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return new JesusFile(f);
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
