package frostscape.type.upgrade;

import arc.struct.Seq;
import frostscape.world.upgrades.UpgradeState;
import frostscape.world.upgrades.UpgradeState.ProgressType;

public interface UpgradeableBuild {


    Seq<UpgradeState> upgradeStates();

    default float getProgress(ProgressType type){
        float total = 0;
        switch (type){
            case PER_ITEM: {
                int each = 0;
                for(UpgradeState state : upgradeStates()){
                    if(state.progress < 1) {
                        total += state.progress;
                        each++;
                    }
                }
                total /= each;
                break;
            }
            case TOTAL: {
                for(UpgradeState state : upgradeStates()){
                    total += state.progress;
                }
                total /= upgradeStates().size;
            }
        }
        return total;
    }
}
