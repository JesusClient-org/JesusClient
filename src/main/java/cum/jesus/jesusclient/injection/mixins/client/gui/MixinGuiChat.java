package cum.jesus.jesusclient.injection.mixins.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiChat.class})
@SideOnly(Side.CLIENT)
public abstract class MixinGuiChat {
    @Shadow
    private boolean waitingOnAutocomplete;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_146406_1_);

    @Inject(method = "sendAutocompleteRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void autoCompleteCmd(String cmd, String ignore, CallbackInfo ci) {
        if (cmd.startsWith(Gui.prefix.getObject())) {
            String[] ls = JesusClient.INSTANCE.commandManager.autoCompletion(cmd).toArray(new String[0]);

            if (ls.length == 0 || cmd.toLowerCase().endsWith(ls[ls.length - 1].toLowerCase())) {
                return;
            }

            waitingOnAutocomplete = true;
            onAutocompleteResponse(ls);
            ci.cancel();
        }
    }
}
