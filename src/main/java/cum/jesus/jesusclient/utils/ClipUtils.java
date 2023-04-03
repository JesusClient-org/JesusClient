package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;

public class ClipUtils {
    /**
     * Teleports player to the given position
     */
    public static void teleport(double x, double y, double z) {
        JesusClient.mc.thePlayer.setPosition(x, y, z);
    }

    public static void clip(double x, double y, double z) {
        teleport(JesusClient.mc.thePlayer.posX + x, JesusClient.mc.thePlayer.posY + y, JesusClient.mc.thePlayer.posZ + z);
    }

    public static void hClip(double distance, float yaw, double yOffs) {
        clip(-Math.sin(yaw*Math.PI/180) * distance, yOffs, Math.cos(yaw*Math.PI/180) * distance);
    }

    public static void hClip(double distance, float yaw) {
        hClip(distance, yaw, 0.0);
    }

    public static void hClip(double distance, double yOffs) {
        hClip(distance, JesusClient.mc.thePlayer.rotationYaw % 360F, yOffs);
    }

    public static void hClip(double distance) {
        hClip(distance, JesusClient.mc.thePlayer.rotationYaw % 360F, 0.0);
    }

    public static void dClip(double distance, float yaw, float pitch) {
        clip(-Math.sin(yaw*Math.PI/180) * Math.cos(pitch) * distance, -Math.sin(pitch) * distance, Math.cos(yaw) * Math.cos(pitch) * distance);
    }

    public static void dClip(double distance, float yaw) {
        dClip(distance, yaw, 0f);
    }

    public static void dClip(double distance) {
        dClip(distance, JesusClient.mc.thePlayer.rotationYaw % 360F);
    }
}
