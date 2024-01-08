package main.world.blocks.environment;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class ParticleFloor extends Floor {

    public Effect effect;

    public float chance;

    public boolean emitBlocked;

    public ParticleFloor(String name) {
        super(name);
        effect = Fx.smoke;
        chance = 0.05f;
        emitBlocked = false;
    }

    public ParticleFloor(String name, int variants) {
        super(name, variants);
    }

    @Override
    public boolean updateRender(Tile tile) {
        return true;
    }

    @Override
    public void renderUpdate(UpdateRenderState tile) {
        //gota check twice cause stuff can block the floor
        if(Mathf.chanceDelta(chance) && (emitBlocked || tile.tile.block() == Blocks.air)) {
            effect.at(tile.tile.worldx(), tile.tile.worldy());
        }
    }
}
