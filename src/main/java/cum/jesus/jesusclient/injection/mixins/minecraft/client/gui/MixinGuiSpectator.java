package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.events.Render2DEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.JesusClient;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(GuiSpectator.class)
public class MixinGuiSpectator {

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        EventManager.call(new Render2DEvent());
    }

}