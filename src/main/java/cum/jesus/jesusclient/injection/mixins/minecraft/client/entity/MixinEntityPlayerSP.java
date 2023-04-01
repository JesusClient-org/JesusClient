package cum.jesus.jesusclient.injection.mixins.minecraft.client.entity;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.MotionUpdateEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {EntityPlayerSP.class}, priority = 1)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
    private double cachedX;
    private double cachedY;
    private double cachedZ;

    private float cachedRotationPitch;
    private float cachedRotationYaw;

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        cachedX = posX;
        cachedY = posY;
        cachedZ = posZ;

        cachedRotationYaw = rotationYaw;
        cachedRotationPitch = rotationPitch;

        MotionUpdateEvent event = new MotionUpdateEvent(EventType.PRE, posX, posY, posZ, rotationYaw, rotationPitch, onGround);

        EventManager.call(event);

        posX = event.getX();
        posY = event.getY();
        posZ = event.getZ();

        rotationYaw = event.getYaw();
        rotationPitch = event.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        posX = cachedX;
        posY = cachedY;
        posZ = cachedZ;

        rotationYaw = cachedRotationYaw;
        rotationPitch = cachedRotationPitch;

        EventManager.call(new MotionUpdateEvent(EventType.POST, posX, posY, posZ, rotationYaw, rotationPitch, onGround));
    }
}
