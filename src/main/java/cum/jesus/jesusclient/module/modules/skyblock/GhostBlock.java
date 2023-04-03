package cum.jesus.jesusclient.module.modules.skyblock;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.GameTickEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.List;

public class GhostBlock extends Module {
    private NumberSetting<Integer> range = new NumberSetting<>("Range", 4, 1, 32);

    public GhostBlock() {
        super("Ghost Block", "Creates ghost blocks at your cursor", Category.SKYBLOCK);
    }

    private List<Block> blacklist = Arrays.asList(
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.chest,
            Blocks.ender_chest,
            Blocks.trapped_chest,
            Blocks.lever
    );

    @Override
    public void handleKeybind() {}

    @EventTarget
    public void onTick(GameTickEvent event) {
        if (!isToggled()) return;

        if (event.getEventType() != EventType.PRE || JesusClient.display  != null) return;
        if (getKeybind() > 0 && !Keyboard.isKeyDown(getKeybind())) return;
        if (getKeybind() < 0 && !Mouse.isButtonDown(getKeybind() + 100)) return;
        if (!mc.inGameHasFocus) return;

        BlockPos lookingAt = mc.thePlayer.rayTrace(range.getObject(), 1f).getBlockPos();

        if (lookingAt != null) {
            Block block =  mc.theWorld.getBlockState(lookingAt).getBlock();

            if (!blacklist.contains(block)) {
                mc.theWorld.setBlockToAir(lookingAt);
            }
        }
    }
}
