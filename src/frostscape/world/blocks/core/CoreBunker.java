package frostscape.world.blocks.core;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.Frostscape;
import frostscape.content.Palf;
import frostscape.math.Mathh;
import frostscape.world.UpgradesType;
import frostscape.world.light.LightBeams;
import frostscape.world.light.Lightc;
import frostscape.world.light.WorldShape;
import frostscape.world.light.shape.WorldPoly;
import frostscape.world.light.shape.WorldRect;
import mindustry.ui.Bar;

import static mindustry.Vars.tilesize;

public class CoreBunker extends BuildBeamCore{

    public float[] hitboxEdges;

    //Conversion of R value in light to steamgenWarmup
    public float lightEfficiency = 0.004f, steamgenWarmdown = 0.002f;
    public float heatLoss = 0.0001f;
    public float powerProduction = 5;

    public CoreBunker(String name) {
        super(name);
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar(Core.bundle.get("stat.warmup"), entity -> new Bar(() -> Core.bundle.get("stat.charge"), () -> Palf.heat, entity::warmup));
    }

    public class CoreBunkerBuild extends BuildBeamCoreBuild implements Lightc {

        public float steamgenWarmup = 0, productionEfficiency = 0;
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
        }

        @Override
        public void created() {
            Frostscape.lights.handle(this);
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
            Log.info(color.r);
            if(!Mathf.zero(color.r))steamgenWarmup = Mathf.clamp(steamgenWarmup + color.r * lightEfficiency);
            collision.after = new LightBeams.ColorData(0, 0, 0);
            return collision;
        }

        @Override
        public float getPowerProduction() {
            return powerProduction * productionEfficiency;
        }
    }
}
