package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.file.builder.FileBuilder;
import cum.jesus.jesusclient.file.reader.FileReader;
import cum.jesus.jesusclient.setting.Setting;

import java.util.List;

public interface IConfigurable {
    String getName();

    String getFileName(); // path from the jesusclient config root (.minecraft/jesusclient/config/) where the config file is stored

    List<Setting> getSettings();

    void writeSpecial(FileBuilder builder);

    void readSpecial(FileReader reader);
}
