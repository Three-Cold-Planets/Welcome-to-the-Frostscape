package frostscape.world.module;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.Frostscape;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.Upgradeable;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.UpgradesType;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.type.ItemStack;
import mindustry.world.modules.BlockModule;

public class UpgradeModule extends BlockModule {

    public Seq<UpgradeState> states = new Seq<>();

    public UpgradeModule(){

    }

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

    public void startUpgrade(UpgradeEntry entry) {
        UpgradeState current = states.find(state -> state.upgrade == entry.upgrade);
        //if no state is found create a new one
        if(current == null){
            states.add(new UpgradeState(entry.upgrade, entry.costs[0]));
            return;
        }
        //Don't create a new state if it's maxed
        if(current.level == entry.stacks()) return;
        //start on the next stack
        current.progress = 0;
        current.level++;
        current.cost = entry.costs[current.level];
    }
}
