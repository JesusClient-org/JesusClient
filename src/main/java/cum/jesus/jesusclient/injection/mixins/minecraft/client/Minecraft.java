package cum.jesus.jesusclient.injection.mixins.minecraft.client;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.client.main.GameConfiguration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.Minecraft.class)
@SideOnly(Side.CLIENT)
public final class Minecraft {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(GameConfiguration config, CallbackInfo ci) {
        JesusClient.init();
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
        JesusClient.instance.start();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void shutdown(CallbackInfo ci) {
        JesusClient.instance.stop();
    }
}
