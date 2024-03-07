package cum.jesus.jesusclient.file;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;

/**
 * Abstract representation of a .jesus data file. <br>
 * Unlike {@link java.io.File}, this does not represent a pathname, but an existing file. <br><br>
 *
 * Can only be instantiated by {@link cum.jesus.jesusclient.file.FileManager}
 */
public final class JesusFile {
    private final File file;

    JesusFile(File file) {
        this.file = file;
    }

    public long length() {
        return file.length();
    }

    public String getNameNoExt() {
        return FilenameUtils.removeExtension(file.getName());
    }

    public byte[] read() throws OutOfMemoryError {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return null;
        }
    }

    public void write(final byte[] bytes) {
        try {
            Files.write(file.toPath(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
