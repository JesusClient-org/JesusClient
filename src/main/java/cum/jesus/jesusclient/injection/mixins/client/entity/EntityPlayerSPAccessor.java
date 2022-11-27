package cum.jesus.jesusclient.injection.mixins.client.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityPlayerSP.class})
public interface EntityPlayerSPAccessor {
    @Accessor
    double getLastReportedPosX();

    @Accessor
    void setLastReportedPosX(double paramDouble);

    @Accessor
    double getLastReportedPosY();

    @Accessor
    void setLastReportedPosY(double paramDouble);

    @Accessor
    double getLastReportedPosZ();

    @Accessor
    void setLastReportedPosZ(double paramDouble);

    @Accessor
    float getLastReportedYaw();

    @Accessor
    void setLastReportedYaw(float paramFloat);

    @Accessor
    float getLastReportedPitch();

    @Accessor
    void setLastReportedPitch(float paramFloat);

    @Accessor
    void setServerSprintState(boolean paramBoolean);

    @Accessor
    boolean getServerSprintState();

    @Accessor
    void setServerSneakState(boolean paramBoolean);

    @Accessor
    boolean getServerSneakState();
}
