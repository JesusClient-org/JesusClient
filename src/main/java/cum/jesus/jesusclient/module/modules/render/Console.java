package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;

import javax.swing.*;

public class Console extends Module {
    public static Console INSTANCE = new Console();

    public Console() {
        super("Console", "An external console that will allow you to control Jesus Client with just text inputs (WIP)", Category.RENDER);
    }

    @Override
    public void onEnable() {
        cum.jesus.jesusclient.gui.externalconsole.Console.start();
    }

    @Override
    public void onDisable() {
        cum.jesus.jesusclient.gui.externalconsole.Console.close();
    }

    @Override
    public boolean isPremiumFeature() {
        return true;
    }
}
