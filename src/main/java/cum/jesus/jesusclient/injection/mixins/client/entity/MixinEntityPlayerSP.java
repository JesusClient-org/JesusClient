package cum.jesus.jesusclient.injection.mixins.client.entity;

import cum.jesus.jesusclient.events.MotionUpdateEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.injection.mixins.entity.MixinEntity;
import cum.jesus.jesusclient.module.modules.combat.KillAura;
import cum.jesus.jesusclient.utils.RotationUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;
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
        posX = cachedX;
        posY = cachedY;
        posZ = cachedZ;

        rotationYaw = cachedRotationYaw;
        rotationPitch = cachedRotationPitch;

        EventManager.call(new MotionUpdateEvent(EventType.POST, posX, posY, posZ, rotationYaw, rotationPitch, onGround));
    }
}
