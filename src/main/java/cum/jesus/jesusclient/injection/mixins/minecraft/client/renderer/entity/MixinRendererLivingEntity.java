package cum.jesus.jesusclient.injection.mixins.minecraft.client.renderer.entity;

import cum.jesus.jesusclient.events.RenderAliveMobEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RendererLivingEntity.class})
public class MixinRendererLivingEntity {
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At(value = "HEAD"), cancellable = true)
    private void doRenderPre(EntityLivingBase entity, double x, double y, double z, float p_doRender_5_, float p_doRender_6_, CallbackInfo ci) {
        RenderAliveMobEvent event = new RenderAliveMobEvent(EventType.PRE, entity, x, y, z);
        EventManager.call(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RenderLivingEvent$Post;<init>(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/entity/RendererLivingEntity;DDD)V", shift = At.Shift.BEFORE))
    private void doRenderPost(EntityLivingBase entity, double x, double y, double z, float p_doRender_5_, float p_doRender_6_, CallbackInfo ci) {
        EventManager.call(new RenderAliveMobEvent(EventType.POST, entity, x, y, z));
    }
}
