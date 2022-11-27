package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.injection.mixins.client.entity.EntityPlayerSPAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtils {
    public static float[] getAngles(Vec3 vec) {
        double diffX = vec.xCoord - JesusClient.mc.thePlayer.posX;
        double diffY = vec.yCoord - JesusClient.mc.thePlayer.posY + JesusClient.mc.thePlayer.getEyeHeight();
        double diffZ = vec.zCoord - JesusClient.mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] { JesusClient.mc.thePlayer.rotationYaw +
                MathHelper.wrapAngleTo180_float(yaw - JesusClient.mc.thePlayer.rotationYaw), JesusClient.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - JesusClient.mc.thePlayer.rotationPitch) };
    }

    public static float[] getServerAngles(Vec3 vec) {
        double diffX = vec.xCoord - JesusClient.mc.thePlayer.posX;
        double diffY = vec.yCoord - JesusClient.mc.thePlayer.posY + JesusClient.mc.thePlayer.getEyeHeight();
        double diffZ = vec.zCoord - JesusClient.mc.thePlayer.posZ;
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] { ((EntityPlayerSPAccessor)JesusClient.mc.thePlayer)
                .getLastReportedYaw() + MathHelper.wrapAngleTo180_float(yaw - ((EntityPlayerSPAccessor)JesusClient.mc.thePlayer).getLastReportedYaw()), ((EntityPlayerSPAccessor)JesusClient.mc.thePlayer)
                .getLastReportedPitch() + MathHelper.wrapAngleTo180_float(pitch - ((EntityPlayerSPAccessor)JesusClient.mc.thePlayer).getLastReportedPitch()) };
    }

    public static float[] getBowAngles(Entity entity) {
        double xDelta = (entity.posX - entity.lastTickPosX) * 0.4D;
        double zDelta = (entity.posZ - entity.lastTickPosZ) * 0.4D;
        double d = JesusClient.mc.thePlayer.getDistanceToEntity(entity);
        d -= d % 0.8D;
        double xMulti = d / 0.8D * xDelta;
        double zMulti = d / 0.8D * zDelta;
        double x = entity.posX + xMulti - JesusClient.mc.thePlayer.posX;
        double z = entity.posZ + zMulti - JesusClient.mc.thePlayer.posZ;
        double y = JesusClient.mc.thePlayer.posY + JesusClient.mc.thePlayer.getEyeHeight() - entity.posY + entity.getEyeHeight();
        double dist = JesusClient.mc.thePlayer.getDistanceToEntity(entity);
        float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0F;
        double d1 = MathHelper.sqrt_double(x * x + z * z);
        float pitch = (float)-(Math.atan2(y, d1) * 180.0D / Math.PI) + (float)dist * 0.11F;
        return new float[] { yaw, -pitch };
    }

    public static boolean isWithinFOV(EntityLivingBase entity, double fov) {
        float yawDifference = Math.abs(getAngles((Entity)entity)[0] - JesusClient.mc.thePlayer.rotationYaw);
        return (yawDifference < fov && yawDifference > -fov);
    }

    public static float getYawDifference(EntityLivingBase entity1, EntityLivingBase entity2) {
        return Math.abs(getAngles((Entity)entity1)[0] - getAngles((Entity)entity2)[0]);
    }

    public static float getYawDifference(EntityLivingBase entity1) {
        return Math.abs(JesusClient.mc.thePlayer.rotationYaw - getAngles((Entity)entity1)[0]);
    }

    public static boolean isWithinPitch(EntityLivingBase entity, double pitch) {
        float pitchDifference = Math.abs(getAngles((Entity)entity)[1] - JesusClient.mc.thePlayer.rotationPitch);
        return (pitchDifference < pitch && pitchDifference > -pitch);
    }

    public static float[] getAngles(Entity en) {
        return getAngles(new Vec3(en.posX, en.posY + en.getEyeHeight() - en.height / 1.5D + 0.5D, en.posZ));
    }

    public static float[] getServerAngles(Entity en) {
        return getServerAngles(new Vec3(en.posX, en.posY + en.getEyeHeight() - en.height / 1.5D + 0.5D, en.posZ));
    }
}
