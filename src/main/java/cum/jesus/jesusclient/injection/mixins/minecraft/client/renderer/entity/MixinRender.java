package cum.jesus.jesusclient.injection.mixins.minecraft.client.renderer.entity;

import cum.jesus.jesusclient.module.modules.render.NoSlimes;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Render.class})
public class MixinRender {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private void injectShadowRender(Entity entity, double p_renderShadow_2_, double p_renderShadow_3_, double p_renderShadow_4_, float p_renderShadow_5_, float p_renderShadow_6_, CallbackInfo ci) {
        if (entity instanceof EntitySlime && NoSlimes.INSTANCE.isToggled() && SkyblockUtils.onSkyblock) {
            if (NoSlimes.INSTANCE.homeOnly.getObject() && !SkyblockUtils.onPrivateIsland) return;
            ci.cancel();
        }
    }
}
