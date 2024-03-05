package cum.jesus.jesusclient.file;

import java.io.File;

/**
 * Abstract representation of a .jesus data file <br>
 * Unlike {@link java.io.File}, this does not represent a pathname, but an existing file <br><br>
 *
 * Can only be instantiated by {@link cum.jesus.jesusclient.file.FileManager}
 */
public final class JesusFile {
    private final File file;

    JesusFile(File file) {
        this.file = file;
    }
}
