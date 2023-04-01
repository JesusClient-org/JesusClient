package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.ChatEvent;
import cum.jesus.jesusclient.events.DrawBackgroundEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.DesktopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
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
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        IChatComponent component = new ChatComponentText(msg);

        if (msg.startsWith(Gui.prefix.getObject()) && msg.length() > 1) {
            if (JesusClient.INSTANCE.commandManager.execute(msg)) {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }

            ci.cancel();
        }

        ChatEvent event = new ChatEvent(EventType.SEND, component);
        EventManager.call(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "drawDefaultBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawWorldBackground(I)V", shift = At.Shift.AFTER))
    private void drawBackground(CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        EventManager.call(new DrawBackgroundEvent(this.mc.currentScreen));
    }
}
