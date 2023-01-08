package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.events.ChatEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;

public class Console extends Module {
    public static BooleanSetting shouldChatLog = new BooleanSetting("Log chat", false, true);

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

    @EventTarget
    public void chat(ChatEvent event) {
        if (isToggled() && shouldChatLog.getObject()) {
            if (event.getEventType() == EventType.RECIEVE) {
                cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println("[Minecraft Chat]" + event.getMessage().getUnformattedText(), false);
            }
        }
    }
}
