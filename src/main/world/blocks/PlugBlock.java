package main.world.blocks;

import arc.Core;
import arc.struct.Seq;
import main.world.BaseBlock;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Tile;

import java.util.Iterator;

public class PlugBlock extends BaseBlock {
    public Seq validFloors;
    protected static float returnCount = 0;

    public PlugBlock(String name) {
        super(name);
        validFloors = Seq.with();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            countSockets(tile);
            if(returnCount > 0) return true;

            //No Tiles of interest found, return here
            return false;
        } else {
            return validFloors.contains(tile.floor());
        }
    }

    protected void countSockets(Tile tile) {
        returnCount = 0;

        Iterator var2 = tile.getLinkedTilesAs(this, tempTiles).iterator();

        while(var2.hasNext()) {
            Tile other = (Tile)var2.next();
            if (validFloors.contains(other.floor())) {
                returnCount++;
            }
        }
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {

        drawPotentialLinks(x, y);
        drawOverlay((float)(x * 8) + offset, (float)(y * 8) + offset, rotation);

        Tile tile = Vars.world.tile(x, y);
        if (tile != null) {
            countSockets(tile);
            if(returnCount > 0) drawPlaceText(Core.bundle.format("bar.socketsfound", returnCount), x, y, valid);
            else drawPlaceText(Core.bundle.get("bar.nosockets"), x, y, valid);
        }
    }
}
