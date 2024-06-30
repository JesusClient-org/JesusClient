package cum.jesus.jesusclient.setting;

import java.util.UUID;

import static cum.jesus.jesusclient.JesusClient.mc;

public final class User {
    public static String username = mc.getSession().getUsername();
    public static UUID uuid = mc.getSession().getProfile().getId();
    public static String uuidString = uuid.toString();
    public static String compactUUIDString = uuidString.replace("-", "");
}
