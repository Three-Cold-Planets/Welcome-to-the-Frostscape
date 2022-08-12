package frostscape.world;

import arc.struct.Seq;
import frostscape.type.upgrade.UpgradeableBuild;
import frostscape.world.upgrades.UpgradeState;
import frostscape.world.upgrades.UpgradeState.ProgressType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.List;

public class FrostscapeBuilding extends Building implements UpgradeableBuild {
    public FrostscapeBlock fblock;

    public Seq<UpgradeState> upgradeStates;

    @Override
    public Building create(Block block, Team team) {
        if(block instanceof FrostscapeBlock) fblock = (FrostscapeBlock) block;
        return super.create(block, team);
    }

    @Override
    public Seq<UpgradeState> upgradeStates(){
        return upgradeStates;
    }

    @Override
    public float getProgress(ProgressType type){
        float total = 0;
        switch (type){
            case PER_ITEM: {
                int each = 0;
                for(UpgradeState state : upgradeStates){
                    if(state.progress < 1) {
                        total += state.progress;
                        each++;
                    }
                }
                total /= each;
                break;
            }
            case TOTAL: {
                for(UpgradeState state : upgradeStates){
                    total += state.progress;
                }
                total /= upgradeStates.size;
            }
        }
        return total;
    }
}
