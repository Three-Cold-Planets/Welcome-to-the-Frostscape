package frostscape.world.blocks.environment;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
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
        return emitBlocked || tile.block() == Blocks.air;
    }
    @Override
    public void renderUpdate(UpdateRenderState tile) {
        if(Mathf.chanceDelta(chance)) {
            effect.at(tile.tile.worldx(), tile.tile.worldy());
        }
    }
}
