package cum.jesus.jesusclient.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.GameTickEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class SkyblockUtils {
    public static boolean inBlood;
    private static Minecraft mc = Minecraft.getMinecraft();
    public static boolean inDungeon;
    public static int lastReportedSlot;
    public static boolean onSkyblock;

    public static void updateItemNoEvent() {
        if (lastReportedSlot != mc.thePlayer.inventory.currentItem) {
            PacketShit.sendPacketNoEvent((Packet<?>)new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            lastReportedSlot = mc.thePlayer.inventory.currentItem;
        }
    }

    @EventTarget
    public void onTick(GameTickEvent event) {
        if (JesusClient.mc.theWorld != null)
            inDungeon = (hasLine("Cleared") || hasLine("Start"));
        onSkyblock = isOnSkyBlock();
    }

    public static boolean isTeam(EntityLivingBase e, EntityLivingBase e2) {
        if (e.getDisplayName().getUnformattedText().length() < 4)
            return false;
        if (e.getDisplayName().getFormattedText().charAt(2) == '§' && e2.getDisplayName().getFormattedText().charAt(2) == '§') {
            if (onSkyblock)
                return true;
            return (e.getDisplayName().getFormattedText().charAt(3) == e2.getDisplayName().getFormattedText().charAt(3));
        }
        return false;
    }

    public static boolean hasLine(String line) {
        try {
            Scoreboard sb = (Minecraft.getMinecraft()).thePlayer.getWorldScoreboard();
            List<Score> list = new ArrayList<>(sb.getSortedScores(sb.getObjectiveInDisplaySlot(1)));
            for (Score score : list) {
                String s;
                ScorePlayerTeam team = sb.getPlayersTeam(score.getPlayerName());
                try {
                    s = ChatFormatting.stripFormatting(team.getColorPrefix() + score.getPlayerName() + team.getColorSuffix());
                } catch (Exception e) {
                    return false;
                }
                StringBuilder builder = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (c < 'Ā')
                        builder.append(c);
                }
                if (builder.toString().toLowerCase().contains(line.toLowerCase()))
                    return true;
                try {
                    s = ChatFormatting.stripFormatting(team.getColorPrefix() + team.getColorSuffix());
                } catch (Exception e) {
                    return false;
                }
                builder = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (c < 'Ā')
                        builder.append(c);
                }
                if (builder.toString().toLowerCase().contains(line.toLowerCase()))
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isOnSkyBlock() {
        try {
            ScoreObjective titleObjective = mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1);
            if (mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(0) != null)
                return ChatFormatting.stripFormatting(mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(0).getDisplayName()).contains("SKYBLOCK");
            return ChatFormatting.stripFormatting(mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNPC(Entity entity) {
        if (!(entity instanceof net.minecraft.client.entity.EntityOtherPlayerMP))
            return false;
        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
        return (entity.getUniqueID().version() == 2 && entityLivingBase.getHealth() == 20.0F);
    }

    public static String getSkyBlockID(ItemStack item) {
        if(item != null) {
            NBTTagCompound extraAttributes = item.getSubCompound("ExtraAttributes", false);
            if(extraAttributes != null && extraAttributes.hasKey("id")) {
                return extraAttributes.getString("id");
            }
        }
        return "";
    }
}
