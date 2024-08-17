package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.file.builder.FileBuilder;
import cum.jesus.jesusclient.file.reader.FileReader;
import cum.jesus.jesusclient.util.Logger;

import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {
    private List<IConfigurable> configurables = new ArrayList<>();

    public void load() {
        FileReader fileReader = new FileReader();
        ConfigReader reader = new ConfigReader(fileReader);

        for (IConfigurable configurable : configurables) {
            try {
                reader.setObject(configurable);
                reader.readConfig();
            } catch (Exception e) {
                Logger.warn("Potential config bug. Loading failed with error: \"" + e.getMessage() + '"');

                if (JesusClient.instance.devMode) {
                    throw e;
                }
            }
        }
    }

    public void save() {
        FileBuilder fileBuilder = new FileBuilder();
        ConfigBuilder builder = new ConfigBuilder(fileBuilder);

        fileBuilder.finish();

        for (IConfigurable configurable : configurables) {
            builder.setObject(configurable);
            builder.writeConfig();
            builder.finish();
        }
    }

    public void register(IConfigurable configurable) {
        configurables.add(configurable);
    }
}