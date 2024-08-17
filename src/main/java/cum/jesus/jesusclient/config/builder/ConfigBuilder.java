package cum.jesus.jesusclient.config.builder;

import cum.jesus.jesusclient.config.IConfigurable;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.file.builder.FileBuilder;
import cum.jesus.jesusclient.setting.Setting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ConfigBuilder {
    private FileBuilder fileBuilder;    // file builder to put the object in
    private IConfigurable object;       // the object currently being written

    private boolean writable = false;   // can fields be written

    // the config builder will completely take over control of the file builder, so using it outside the config builder is not advised
    public ConfigBuilder(FileBuilder fileBuilder) {
        this.fileBuilder = fileBuilder;

        fileBuilder.reset();
    }

    public void setObject(IConfigurable object) {
        fileBuilder.finished();

        this.object = object;
        fileBuilder.reset();
    }

    public void writeConfig() {
        fileBuilder.createString(object.getName());

        object.writeSpecial(fileBuilder);

        List<Setting> settings = object.getSettings();
        fileBuilder.addInt(settings.size());

        writable = true;

        for (Setting setting : settings) {
            setting.addToBuilder(this);
        }
    }

    public void addBoolean(String name, boolean b) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 1);
        fileBuilder.addBoolean(b);
    }

    public void addByte(String name, byte b) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 1);
        fileBuilder.addByte(b);
    }

    public void addShort(String name, short s) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 2);
        fileBuilder.addShort(s);
    }

    public void addInt(String name, int i) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 4);
        fileBuilder.addInt(i);
    }

    public void addLong(String name, long l) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 8);
        fileBuilder.addLong(l);
    }

    public void addFloat(String name, float f) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 4);
        fileBuilder.addFloat(f);
    }

    public void addDouble(String name, double d) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 8);
        fileBuilder.addDouble(d);
    }

    public void addBigInt(String name, BigInteger i) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + i.bitLength() / 8 + 1);
        fileBuilder.addBigInt(i);
    }

    public void addNumber(String name, Number value) {
        writable();

        if (value instanceof Integer) {
            addInt(name, value.intValue());
        } else if (value instanceof Float) {
            addFloat(name, value.floatValue());
        } else if (value instanceof Long) {
            addLong(name, value.longValue());
        } else if (value instanceof Double) {
            addDouble(name, value.longValue());
        } else if (value instanceof Short) {
            addShort(name, value.shortValue());
        } else if (value instanceof Byte) {
            addByte(name, value.byteValue());
        } else if (value instanceof BigInteger) {
            addBigInt(name, (BigInteger) value);
        }
    }

    public void addString(String name, String value) {
        writable();

        fileBuilder.createString(name);
        fileBuilder.addInt(fileBuilder.getPosition() + 4 + value.getBytes(StandardCharsets.UTF_8).length);
        fileBuilder.createString(value);
    }

    public void finish() {
        fileBuilder.finish();

        File file = FileManager.getConfigFile(object.getFileName());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            fileBuilder.print(out);
        } catch (IOException e) {
            throw new RuntimeException("Error with writing out config file.", e);
        }
    }

    public void writable() {
        if (!writable) {
            throw new RuntimeException("config builder is not ready to write fields yet");
        }
    }
}
