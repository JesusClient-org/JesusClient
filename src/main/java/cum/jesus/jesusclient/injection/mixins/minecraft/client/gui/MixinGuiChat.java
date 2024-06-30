package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat {
    @Shadow
    private boolean waitingOnAutocomplete;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_146406_1_);

    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;setMaxStringLength(I)V", shift = At.Shift.AFTER))
    private void initGui(CallbackInfo ci) {
        this.inputField.setMaxStringLength(50000);
    }

    @Inject(method = "sendAutocompleteRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void sendAutoCompleteRequest(String msg, String ignore, CallbackInfo ci) {
        if (!JesusClient.isLoaded()) return;

        if (msg.startsWith(JesusClient.instance.config.commandPrefix.getValue())) {
            String[] ls = JesusClient.instance.commandHandler.autoComplete(msg).toArray(new String[0]);

            if (ls.length == 0 || msg.toLowerCase().endsWith(ls[ls.length - 1].toLowerCase())) {
                return;
            }

            waitingOnAutocomplete = true;
            onAutocompleteResponse(ls);
            ci.cancel();
        }
    }
}
