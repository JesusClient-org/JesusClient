package cum.jesus.jesusclient.module.modules.other;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;

public class MonkeyFlip extends Module {
    public MonkeyFlip() {
        super("Monkey Flip", "Makes you a monkey and flips you", Category.OTHER);
    }

    @Override
    public boolean isPremiumFeature() {
        return true;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
