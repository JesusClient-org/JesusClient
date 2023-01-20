package cum.jesus.jesusclient.injection.mixins.minecraft.client.renderer.entity;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.PlayerScale;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("HEAD"))
    private void playerRender(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (PlayerScale.INSTANCE.isToggled()) {
            //GlStateManager.scale(PlayerScale.INSTANCE.scaleX.getObject(), PlayerScale.INSTANCE.scaleY.getObject(), PlayerScale.INSTANCE.scaleZ.getObject());
        }

        /*
        if (player == JesusClient.mc.thePlayer) {
            //Logger.debug("player rendered");

            GlStateManager.scale(1, -1, 1);
            GlStateManager.rotate(0, 0, -90, 0);
            GlStateManager.translate(0, -1.6, 0);

            i++;
        }
        */
    }
}
