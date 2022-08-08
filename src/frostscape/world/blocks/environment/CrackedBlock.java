package frostscape.world.blocks.environment;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.content.Fxf;
import frostscape.world.blocks.environment.data.FloatEnvData;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
public class CrackedBlock extends Floor {

    public Effect glowEffect = Fxf.glowEffect;
    public float minBlinkTime = 85, maxBlinkTime = 160;
    public Floor cracked = (Floor) Blocks.slag;
    public TextureRegion[] topVariantRegions;

    public CrackedBlock(String name) {
        super(name);
    }

    public TextureRegion[] topVariantRegions() {
        return topVariantRegions == null ? (topVariantRegions = new TextureRegion[]{fullIcon}) : variantRegions;
    }

    @Override
    public void load() {
        super.load();
        if (variants != 0) {
            topVariantRegions = new TextureRegion[variants];

            for(int i = 0; i < variants; ++i) {
                topVariantRegions[i] = Core.atlas.find(name + "-top" + (i + 1));
            }
        }
    }

    public float crackTime = 100;

    @Override
    public boolean updateRender(Tile tile) {
        return true;
    }

    @Override
    public void renderUpdate(UpdateRenderState tile) {
        super.renderUpdate(tile);

        int index = Mathf.randomSeed(tile.tile.pos(), 0, Math.max(0, variantRegions.length - 1));

        float blinkTime = Mathf.randomSeed(tile.tile.pos(), minBlinkTime, maxBlinkTime);

        tile.data += Time.delta;
        if(tile.data >= blinkTime) {
            tile.data = 0;
            glowEffect.lifetime = blinkTime;
            glowEffect.create(tile.tile.worldx(), tile.tile.worldy(), 0, Color.white, topVariantRegions[index]);
        }
    }
}
