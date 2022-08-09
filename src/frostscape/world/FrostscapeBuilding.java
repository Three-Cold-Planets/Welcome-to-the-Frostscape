package frostscape.world;

import frostscape.world.upgrades.UpgradeState;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.List;

public class FrostscapeBuilding extends Building {
    public FrostscapeBlock fblock;

    public List<UpgradeState> upgradeStates;

    @Override
    public Building create(Block block, Team team) {
        if(block instanceof FrostscapeBlock) fblock = (FrostscapeBlock) block;
        return super.create(block, team);
    }
}
