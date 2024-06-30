package cum.jesus.jesusclient.file;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Abstract representation of a .jesus data file. <br>
 * Unlike {@link java.io.File}, this does not represent a pathname, but an existing file. <br><br>
 *
 * Can only be instantiated by {@link cum.jesus.jesusclient.file.FileManager}
 */
public final class JesusFile {
    private final File file;
    private final FileOutputStream appender;

    JesusFile(File file) {
        this.file = file;

        try {
            appender = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public long length() {
        return file.length();
    }

    public String getName() {
        return file.getName();
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

    public void clear() {
        write(new byte[0]);
    }

    public void write(final byte[] bytes) {
        try {
            Files.write(file.toPath(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(final JesusSerializable serializable) {
        write(serializable.toBytes());
    }

    public void append(final byte[] bytes) {
        try {
            appender.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void append(final JesusSerializable serializable) {
        append(serializable.toBytes());
    }
}
