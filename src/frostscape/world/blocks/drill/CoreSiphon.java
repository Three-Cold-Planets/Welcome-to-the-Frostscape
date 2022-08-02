package frostscape.world.blocks.drill;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.modules.LiquidModule.LiquidConsumer;

public class CoreSiphon extends Drill {

    public float liquidPadding = 0.0F;
    public Liquid slagLiquid = Liquids.slag;
    public TextureRegion topRegion, bottomRegion, slagRegion;
    public TextureRegion[] beamRegions;
    public float beamMoveDst = 9, beamSpeed = 65f;
    public ConsumeLiquid boost;

    public int beams = 4;
    public CoreSiphon(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find(name, Core.atlas.find("clear")));
        bottomRegion = Core.atlas.find(name + "-bottom", Core.atlas.find(name, Core.atlas.find("clear")));
        slagRegion = Core.atlas.find(name + "-slag", Core.atlas.find(name, Core.atlas.find("clear")));
        beamRegions = new TextureRegion[beams];
        for (int i = 0; i < beams; i++) {
            beamRegions[i] = Core.atlas.find(name + "-beam" + (i));
        }
    }

    public class CoreSiphonBuild extends DrillBuild{

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid);
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            if (this.liquids.get(boost.liquid) > 0.001F) {
                LiquidBlock.drawTiledFrames(size, this.x, this.y, liquidPadding, this.liquids.current(), this.liquids.currentAmount()/liquidCapacity);
            }
            float slagAmount = liquids.get(slagLiquid)/liquidCapacity;
            Drawf.liquid(slagRegion, x, y, slagAmount, slagLiquid.color);

            Draw.alpha(1);

            Draw.rect(beamRegions[0], x, y - Mathf.sin(timeDrilled, beamSpeed, beamMoveDst), 0);

            Draw.rect(beamRegions[1], x + Mathf.sin(timeDrilled + 45, beamSpeed, beamMoveDst), y, 0);

            Draw.rect(beamRegions[2], x, y + Mathf.sin(timeDrilled + 90, beamSpeed, beamMoveDst), 0);

            Draw.rect(beamRegions[3], x - Mathf.sin(timeDrilled + 135, beamSpeed, beamMoveDst), y, 0);

            Draw.rect(topRegion, x, y, 0);
        }
    }
}
