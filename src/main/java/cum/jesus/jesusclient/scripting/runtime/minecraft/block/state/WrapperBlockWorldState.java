package cum.jesus.jesusclient.scripting.runtime.minecraft.block.state;

import cum.jesus.jesusclient.scripting.runtime.minecraft.util.WrapperBlockPos;
import net.minecraft.block.state.BlockWorldState;

public class WrapperBlockWorldState {
    private BlockWorldState real;

    public WrapperBlockWorldState(BlockWorldState var1) {
        this.real = var1;
    }

    public BlockWorldState unwrap() {
        return this.real;
    }

    public WrapperIBlockState getBlockState() {
        return new WrapperIBlockState(this.real.getBlockState());
    }

    public WrapperBlockPos getPos() {
        return new WrapperBlockPos(this.real.getPos());
    }
}
