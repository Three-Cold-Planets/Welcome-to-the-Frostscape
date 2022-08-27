package frostscape.world.module;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.Upgradeable;
import frostscape.world.upgrades.UpgradeState;
import mindustry.world.modules.BlockModule;

public class UpgradeModule extends BlockModule {

    public Seq<UpgradeState> states = new Seq<>();

    public void update(Upgradeable u){
        states.each(s -> {
            u.applyDeltas(s);
        });
    }

    @Override
    public void write(Writes write){
        write.i(states.size);
        states.each(s -> s.write(write));
    }

    @Override
    public void read(Reads read, boolean legacy) {
        int size = read.i();
        for (int i = 0; i < size; i++) {
            states.add(new UpgradeState().read(read));
        }
    }

    public float getProgress(UpgradeState.ProgressType type){
        float total = 0;
        switch (type){
            case PER_ITEM: {
                int each = 0;
                for(UpgradeState state : states){
                    if(state.progress < 1) {
                        total += state.progress;
                        each++;
                    }
                }
                total /= each;
                break;
            }
            case TOTAL: {
                for(UpgradeState state : states){
                    total += state.progress;
                }
                total /= states.size;
            }
        }
        return total;
    }

    public void startUpgrade(Upgrade upgrade) {
        UpgradeState current = states.find(state -> state.upgrade == upgrade);
        //if no state is found create a new one
        if(current == null){
            states.add(new UpgradeState(upgrade, upgrade.stackCosts[0]));
            return;
        }
        //Don't create a new state if it's maxed
        if(current.level == upgrade.stacks) return;
        //start on the next stack
        current.progress = 0;
        current.level++;
        current.cost = upgrade.stackCosts[current.level];
    }
}
