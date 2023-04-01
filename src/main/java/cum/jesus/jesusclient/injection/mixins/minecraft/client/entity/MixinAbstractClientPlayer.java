package cum.jesus.jesusclient.injection.mixins.minecraft.client.entity;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.injection.mixins.minecraft.entity.player.MixinEntityPlayer;
import cum.jesus.jesusclient.remote.Capes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = Integer.MAX_VALUE)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {
    @Shadow
    private NetworkPlayerInfo playerInfo;

    @Inject(method = "getLocationCape", at = {@At("RETURN")}, cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (!JesusClient.clientLoaded) return;

        ResourceLocation person = Capes.getCape(playerInfo.getGameProfile().getId().toString().replace("-", ""));
        if (person != null) cir.setReturnValue(person);
    }
}