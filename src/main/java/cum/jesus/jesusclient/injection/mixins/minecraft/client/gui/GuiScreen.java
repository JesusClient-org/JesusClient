package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.GuiScreen.class)
@SideOnly(Side.CLIENT)
public final class GuiScreen {
    @Shadow
    public Minecraft mc;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String msg, boolean addToChat, CallbackInfo ci) {
        if (msg.startsWith("-") && msg.length() > 1) {
            if (JesusClient.instance.commandHandler.execute(msg)) {
                mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }

            ci.cancel();
        }
    }
}
