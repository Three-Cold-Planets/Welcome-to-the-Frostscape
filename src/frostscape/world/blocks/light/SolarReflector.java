package frostscape.world.blocks.light;

import arc.struct.Seq;
import frostscape.world.FrostscapeBlock;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.UpgradesType;
import frostscape.world.light.LightBeams;
import frostscape.world.light.LightBeams.LightSource;
import frostscape.world.light.Lightc;

public class SolarReflector extends FrostscapeBlock {

    public SolarReflector(String name) {
        super(name);
    }

    public static class ReflectorSource extends LightSource{
        float x, y;
        public ReflectorSource(LightBeams.ColorData color, float rotation, float x, float y) {
            super(color, rotation);
            this.x = x;
            this.y = y ;
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
        public ReflectorSource source;
        public float rotation = 0;

        @Override
        public void created() {
            source = new ReflectorSource(new LightBeams.ColorData(1, 1, 1), rotation, x, y);
        }

        @Override
        public UpgradesType type() {
            return (UpgradesType) block;
        }

        @Override
        public Seq<LightSource> getSources() {
            return Seq.with(source);
        }

        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            return null;
        }

        @Override
        public LightBeams.WorldShape[] hitboxes() {
            return Lightc.super.hitboxes();
        }

        @Override
        public void afterLight() {
            Lightc.super.afterLight();
        }
    }
}
