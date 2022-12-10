package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;

public class MovementUtils {
    public static boolean isMoving() {
        return (JesusClient.mc.thePlayer.moveForward != 0.0F || JesusClient.mc.thePlayer.moveStrafing != 0.0F);
    }

    public static void bop(double s) {
        double forward = JesusClient.mc.thePlayer.movementInput.moveForward;
        double strafe = JesusClient.mc.thePlayer.movementInput.moveStrafe;
        float yaw = JesusClient.mc.thePlayer.rotationYaw;
        if (forward == 0.0D && strafe == 0.0D) {
            JesusClient.mc.thePlayer.motionX = 0.0D;
            JesusClient.mc.thePlayer.motionZ = 0.0D;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += ((forward > 0.0D) ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += ((forward > 0.0D) ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }
            double rad = Math.toRadians((yaw + 90.0F));
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);

            JesusClient.mc.thePlayer.motionX = forward * s * cos + strafe * s * sin;
            JesusClient.mc.thePlayer.motionZ = forward * s * sin - strafe * s * cos;
        }
    }
}
