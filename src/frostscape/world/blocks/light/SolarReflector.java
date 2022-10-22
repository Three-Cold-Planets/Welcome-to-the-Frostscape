package frostscape.world.blocks.light;

import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.Frostscape;
import frostscape.world.FrostscapeBlock;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.UpgradesType;
import frostscape.world.light.LightBeams;
import frostscape.world.light.LightBeams.LightSource;
import frostscape.world.light.Lightc;
import mindustry.world.draw.DrawBlock;

public class SolarReflector extends FrostscapeBlock {

    public LightBeams.ColorData data = new LightBeams.ColorData(1, 1, 1);
    public float rotationSpeed = 0.001f;

    public DrawBlock drawer;

    public SolarReflector(String name) {
        super(name);

        config(Float.class, (entity, rot) -> {
            SolarReflectorBuild reflector = (SolarReflectorBuild) entity;
            reflector.targetRot = rot;
        });
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    public TextureRegion[] icons() {
        return this.drawer.finalIcons(this);
    }

    public void getRegionsToOutline(Seq<TextureRegion> out) {
        this.drawer.getRegionsToOutline(this, out);
    }

    public static class ReflectorSource extends LightSource{
        float x, y;
        public ReflectorSource(LightBeams.ColorData color, float rotation, float x, float y) {
            super(color, rotation);
            this.x = x;
            this.y = y;
            beamWidth = 23.5f/4/2;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }
    }

    public class SolarReflectorBuild extends FrostscapeBuilding implements Lightc {

        //Instantiated on building creation
        public Seq<ReflectorSource> sources = new Seq<>();
        public float rotation = 0, targetRot = 0;

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void update() {
            super.update();
            rotation += Time.delta;
            sources.each(source -> source.rotation = rotation);

            Angles.moveToward(rotation, targetRot, rotationSpeed);
        }

        @Override
        public void created() {
            sources.add(new ReflectorSource(data, rotation, x, y));
            Frostscape.lights.handle(this);
        }

        @Override
        public boolean exists() {
            return added;
        }

        @Override
        public UpgradesType type() {
            return (UpgradesType) block;
        }

        @Override
        public void getSources(Seq<LightSource> out) {
            out.addAll(sources);
        }

        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            return null;
        }

        @Override
        public void afterLight() {
            Lightc.super.afterLight();
        }
    }
}
