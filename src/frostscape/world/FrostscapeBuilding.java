package frostscape.world;

import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

public class FrostscapeBuilding extends Building {
    public FrostscapeBlock fblock;

    @Override
    public Building create(Block block, Team team) {
        if(block instanceof FrostscapeBlock) fblock = (FrostscapeBlock) block;
        return super.create(block, team);
    }
}
