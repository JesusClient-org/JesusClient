package cum.jesus.jesusclient.module.modules.skyblock;

import cum.jesus.jesusclient.events.ChatEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.StringSetting;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.Utils;

public class ApiKey extends Module {
    public static StringSetting apiKey = new StringSetting("The api key", "no api key");

    public ApiKey() {
        super("Api Key", "Hidden api key module", Category.SKYBLOCK, false, true, 0);
    }

    @EventTarget
    public void chat(ChatEvent event) {
        if (event.getEventType() != EventType.RECIEVE) return;
        if (!Utils.onHypixel) return;

        String formatted = event.getMessage().getFormattedText();
        String unformatted = event.getMessage().getUnformattedText();

        if (unformatted.startsWith("Your new API key is ") && event.getMessage().getSiblings().size() >= 1) {
            String apiKey = event.getMessage().getSiblings().get(0).getChatStyle().getChatClickEvent().getValue();
            ApiKey.apiKey.setObject(apiKey);

            ChatUtils.sendPrefixMessage("Updated your API key to " + apiKey);
        }
    }
}
