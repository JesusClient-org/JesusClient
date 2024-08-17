package cum.jesus.jesusclient.config.reader;

import cum.jesus.jesusclient.config.IConfigurable;
import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.file.reader.FileReader;
import cum.jesus.jesusclient.setting.Setting;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public final class ConfigReader {
    private FileReader fileReader;
    private IConfigurable object;

    private Map<String, Integer> settingOffsets = new HashMap<>();

    private boolean readable = false;
    private boolean noFile = false;

    public ConfigReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public void setObject(IConfigurable object) {
        this.object = object;

        File file = FileManager.getConfigFile(object.getFileName());

        if (!file.exists()) {
            noFile = true;
            return;
        } else {
            noFile = false;
        }

        fileReader.reset(file);
        settingOffsets.clear();
    }

    public void readConfig() {
        if (noFile) return;

        String name = fileReader.getString();
        if (!name.equals(object.getName())) {
            throw new RuntimeException("Config name does not match given configurable");
        }

        object.readSpecial(fileReader);

        int settingCount = fileReader.getInt();
        for (int i = 0; i < settingCount; i++) {
            String settingName = fileReader.getString();
            int position = fileReader.getPosition() + 4;

            settingOffsets.put(settingName, position);

            int nextPosition = fileReader.getInt();
            fileReader.setPosition(nextPosition);
        }

        readable = true;

        for (Setting setting : object.getSettings()) {
            setting.getFromReader(this);
        }
    }

    public boolean getBoolean(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getBoolean(pos);
    }

    public byte getByte(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getByte(pos);
    }

    public short getShort(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getShort(pos);
    }

    public int getInt(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getInt(pos);
    }

    public long getLong(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getLong(pos);
    }

    public float getFloat(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getFloat(pos);
    }

    public double getDouble(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getDouble(pos);
    }

    public BigInteger getBigInt(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getBigInt(pos);
    }

    public String getString(String name) {
        readable();

        Integer pos = settingOffsets.get(name);

        if (pos == null) {
            throw new RuntimeException("setting '" + name + "' not found in configurable '" + object.getName() + "'");
        }

        return fileReader.getString(pos);
    }

    public void readable() {
        if (!readable) {
            throw new RuntimeException("config reader is not ready to read fields yet");
        }
    }
}
