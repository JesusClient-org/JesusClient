package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.events.Render2DEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.module.modules.render.NoBlind;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinGuiIngame {
    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void disablePumpkinOverlay(ScaledResolution p_renderPumpkinOverlay_1_, CallbackInfo ci) {
        if (NoBlind.INSTANCE.isToggled() && NoBlind.INSTANCE.pumpkin.getObject()) ci.cancel();
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        EventManager.call(new Render2DEvent());
    }
}
