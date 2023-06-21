package main.world.blocks.core;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import main.content.Fxf;
import main.graphics.Draww;
import main.graphics.ModPal;
import main.world.UpgradesType;
import main.world.systems.light.LightBeams;
import main.world.systems.light.Lightc;
import main.world.systems.light.WorldShape;
import main.world.systems.light.shape.WorldPoly;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.liquid.LiquidBlock;

public class CoreBunker extends BuildBeamCore{

    public TextureRegion bottomRegion, liquidRegion, shineRegion, topRegion, turbineRegion;

    public Effect steamEffect = Fxf.steamSmoke;

    public float[] hitboxEdges;

    //Conversion of R value in light to steamgenWarmup
    public float lightEfficiency = 0.004f, steamgenWarmdown = 0.002f;
    public float heatLoss = 0.001f;

    public float liquidPadding = 0.0f;
    public float powerProduction = 5;
    public float effectChance = 0.65f;
    public float rotationSpeed = 2.5f;

    public CoreBunker(String name) {
        super(name);
        hasPower = true;
        hasLiquids = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
        liquidRegion = Core.atlas.find(name + "-liquid");
        shineRegion = Core.atlas.find(name + "-shine");
        topRegion = Core.atlas.find(name + "-top");
        turbineRegion = Core.atlas.find(name + "-turbine");
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar(Core.bundle.get("stat.warmup"), entity -> new Bar(() -> Core.bundle.get("stat.charge"), () -> ModPal.heat, entity::warmup));
    }

    public class CoreBunkerBuild extends BuildBeamCoreBuild implements Lightc {

        public float steamgenWarmup = 0, productionEfficiency = 0, totalProgress = 0;
        public WorldPoly hitbox = new WorldPoly(x, y, hitboxEdges);

        @Override
        public float warmup() {
            return steamgenWarmup;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            steamgenWarmup = Mathf.clamp(steamgenWarmup - steamgenWarmdown * Time.delta);
            productionEfficiency = Mathf.lerpDelta(Mathf.clamp(productionEfficiency - heatLoss * Time.delta), 1, steamgenWarmup);
            totalProgress += productionEfficiency;

            if(Mathf.randomBoolean(productionEfficiency * effectChance)) steamEffect.at(x, y);
        }

        @Override
        public void created() {
            LightBeams.get().handle(this);
        }

        @Override
        public boolean exists() {
            return added;
        }

        @Override
        public boolean collides() {
            return true;
        }

        @Override
        public UpgradesType type() {
            return (UpgradesType) block;
        }

        @Override
        public float reflectivity(int shape, int side) {
            return 1;
        }

        @Override
        public void hitboxes(Seq<WorldShape> sequence){
            sequence.add(hitbox.set(x, y, hitboxEdges));
        }
        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            //Fully absorb the effects
            if(!Mathf.zero(color.r))steamgenWarmup = Mathf.clamp(steamgenWarmup + color.r * lightEfficiency);
            collision.after = new LightBeams.ColorData(0, 0, 0);
            return collision;
        }

        @Override
        public float getPowerProduction() {
            return powerProduction * productionEfficiency;
        }

        public void draw() {
            if (this.thrusterTime > 0.0F) {
                float frame = this.thrusterTime;
                Draw.alpha(1.0F);
                this.drawThrusters(frame);
                Draw.rect(this.block.fullIcon, this.x, this.y);
                Draw.alpha(Interp.pow4In.apply(frame));
                this.drawThrusters(frame);
                Draw.reset();
                this.drawTeamTop();
            } else {
                Draw.rect(bottomRegion, x, y);
                Liquid current = liquids.current();
                LiquidBlock.drawTiledFrames(size, x, y, liquidPadding, current, liquids.get(current)/liquidCapacity);
                Draw.alpha(1);
                Draw.color();
                Draw.rect(region, x, y);
                Drawf.spinSprite(turbineRegion, x, y, totalProgress * rotationSpeed);
                Draw.rect(topRegion, x, y);
                Draww.drawShine(shineRegion, x, y, 0, 1);
                drawTeamTop();
                drawMounts();
                drawTeam();
            }
        }
    }
}
