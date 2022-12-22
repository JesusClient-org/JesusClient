package cum.jesus.jesusclient.injection.mixins.minecraft.client.multiplayer;

import cum.jesus.jesusclient.events.WorldLoadEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldClient.class})
public class MixinWorldClient {
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void worldClientConstructor(NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_, EnumDifficulty p_i45063_4_, Profiler p_i45063_5_, CallbackInfo ci) {
        EventManager.call(new WorldLoadEvent());
    }
}
