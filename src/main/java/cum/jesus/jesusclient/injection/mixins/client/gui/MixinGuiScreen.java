package cum.jesus.jesusclient.injection.mixins.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
@SideOnly(Side.CLIENT)
public class MixinGuiScreen {
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String msg, CallbackInfo ci) {
        if (msg.startsWith(Gui.prefix.getObject())) {
            String c = msg.substring(Gui.prefix.getObject().length());
            if (!c.isEmpty()) {
                String cm = c.toLowerCase();
                boolean hasArgs = c.contains(" ");
                String[] args = hasArgs ? c.split(" ") : null;

                //Logger.info("Ran command " + cm);

                JesusClient.INSTANCE.commandManager.executeCommand(cm.split(" ")[0], args);
                JesusClient.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }
            ci.cancel();
        }
    }
}
