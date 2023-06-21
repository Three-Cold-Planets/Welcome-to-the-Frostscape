package main.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.*;

public class SteamVentProp extends SteamVent {

    public Point2[] offsets;
    private static EffectData data;
    public EffectData[] effects;

    public SteamVentProp(String name) {
        super(name);
        placeableOn = false;
    }

    @Override
    public boolean checkAdjacent(Tile tile){
        for(Point2 point : offsets){
            Tile other = Vars.world.tile(tile.x + point.x, tile.y + point.y);
            if(other == null || other.floor() != this){
                return false;
            }
        }
        return true;
    }


    @Override
    public void drawBase(Tile tile){
        parent.drawBase(tile);

        if(checkAdjacent(tile)){
            Mathf.rand.setSeed(tile.pos());
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx(), tile.worldy());
        }
    }

    @Override
    public void renderUpdate(UpdateRenderState state){
        if(state.tile.block() == Blocks.air && Vars.state.isPlaying()){
            for (int i = 0; i < effects.length; i++) {
                data = effects[i];
                if(Mathf.chance(data.chance * Time.delta)){
                    data.effect.at(state.tile.worldx() + data.pos.x + Mathf.range(data.posRand.x), state.tile.worldy() + data.pos.y + Mathf.range(data.posRand.y), data.rotation, data.data != null ? data.data : state.tile);
                }
            }
        }
    }

    public class EffectData{
        public Effect effect;
        public Point2 pos;
        public Point2 posRand;
        public float chance;
        public float rotation;
        public Object data;
    }
}
