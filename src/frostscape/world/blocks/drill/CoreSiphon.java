package frostscape.world.blocks.drill;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import frostscape.graphics.Draww;
import frostscape.math.Mathh;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.modules.LiquidModule.LiquidConsumer;

import static arc.math.Angles.moveToward;

public class CoreSiphon extends Drill {

    public float liquidPadding = 0.0F;
    public Liquid slagLiquid = Liquids.slag;
    public TextureRegion topRegion, bottomRegion, slagRegion, scaffoldingRegion;
    public TextureRegion[] beamRegions;
    public float beamMoveDst = 9, beamSpeed = 65f;
    public ConsumeLiquid boost;

    public int beams = 4;

    public int drills = 4;

    public float circleRadius = 3, inactiveCircleRadius = 0.1f, circleTime = 56, headSpeed = 0.1f;

    public Vec2[] positions;

    public CoreSiphon(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find(name, Core.atlas.find("clear")));
        bottomRegion = Core.atlas.find(name + "-bottom", Core.atlas.find(name, Core.atlas.find("clear")));
        slagRegion = Core.atlas.find(name + "-slag", Core.atlas.find(name, Core.atlas.find("clear")));
        scaffoldingRegion = Core.atlas.find(name + "-scaffolding", Core.atlas.find("clear"));
        beamRegions = new TextureRegion[beams];
        for (int i = 0; i < beams; i++) {
            beamRegions[i] = Core.atlas.find(name + "-beam" + (i));
        }
    }

    public class CoreSiphonBuild extends DrillBuild{

        public Vec2[] drillHeads = new Vec2[drills];

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            for (int i = 0; i < drills; i++) {
                drillHeads[i] = new Vec2();
            }
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void update() {
            super.update();
            for (int i = 0; i < drillHeads.length; i++) {
                float per = timeDrilled + i * 360/drillHeads.length;

                Tmp.v1.set(Mathf.sin(per, circleTime, (efficiency() > 0.001F ? 1 : inactiveCircleRadius)) + positions[i].x, Mathf.sin(per + 90, circleTime, circleRadius) + positions[i].y);
                drillHeads[i] = Mathh.moveToward(drillHeads[i], Tmp.v1, headSpeed);
            }
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

            for (int i = 0; i < drillHeads.length; i++) {
                Draw.rect(rotatorRegion, x + drillHeads[i].x, y + drillHeads[i].y);
                Draww.drawChain(scaffoldingRegion, x, y, x + drillHeads[i].x, y + drillHeads[i].y, 0);
            }

            Draw.rect(beamRegions[0], x, y - Mathf.sin(timeDrilled, beamSpeed, beamMoveDst), 0);

            Draw.rect(beamRegions[1], x + Mathf.sin(timeDrilled + 90, beamSpeed, beamMoveDst), y, 0);

            Draw.rect(beamRegions[2], x, y + Mathf.sin(timeDrilled + 180, beamSpeed, beamMoveDst), 0);

            Draw.rect(beamRegions[3], x - Mathf.sin(timeDrilled + 270, beamSpeed, beamMoveDst), y, 0);

            Draw.rect(topRegion, x, y, 0);
        }
    }
}
