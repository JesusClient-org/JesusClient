package cum.jesus.jesusclient.injection.mixins.minecraft.client;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.event.EventManager;
import cum.jesus.jesusclient.event.EventType;
import cum.jesus.jesusclient.event.events.videogame.GameTickEvent;
import cum.jesus.jesusclient.event.events.videogame.KeyInputEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.Minecraft.class)
@SideOnly(Side.CLIENT)
public final class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

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

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void runTickAfterDispatchKeypresses(CallbackInfo ci) {
        if (!JesusClient.isLoaded()) return;

        if (Keyboard.getEventKeyState() && currentScreen != null) {
            EventManager.call(new KeyInputEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
        }
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPreClientTick()V", shift = At.Shift.BEFORE))
    private void runTickPre(CallbackInfo ci) {
        if (!JesusClient.isLoaded()) return;

        EventManager.call(new GameTickEvent(EventType.PRE));
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPostClientTick()V", shift = At.Shift.BEFORE))
    private void runTickPost(CallbackInfo ci) {
        if (!JesusClient.isLoaded()) return;

        EventManager.call(new GameTickEvent(EventType.POST));
    }
}
