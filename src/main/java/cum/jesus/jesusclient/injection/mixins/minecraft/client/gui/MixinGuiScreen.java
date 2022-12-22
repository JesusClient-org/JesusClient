package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen {
    @Shadow
    public Minecraft mc;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String msg, boolean addToChat, CallbackInfo ci) {
        if (JesusClient.INSTANCE.blacklisted) return;

        if (msg.startsWith(Gui.prefix.getObject()) && msg.length() > 1) {
            if (JesusClient.INSTANCE.commandManager.execute(msg)) {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }

            ci.cancel();
        }
    }
}
