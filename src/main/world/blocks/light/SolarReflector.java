package main.world.blocks.light;

import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.struct.Seq;
import arc.util.Time;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import main.world.systems.light.LightBeams;
import main.world.systems.light.LightBeams.LightSource;
import main.world.systems.light.Lightc;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Unit;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.draw.DrawBlock;

public class SolarReflector extends BaseBlock {

    public LightBeams.ColorData data = new LightBeams.ColorData(1, 1, 1);
    public float rotationSpeed = 1f;

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

    public static class ReflectorSource extends LightSource {
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

    public class SolarReflectorBuild extends BaseBuilding implements Lightc, ControlBlock {

        public BlockUnitc unit = (BlockUnitc) UnitTypes.block.create(team);

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
            if(isControlled() && unit.isShooting()) targetRot = angleTo(unit.aimX(), unit.aimY());
            sources.each(source -> source.rotation = rotation);

            rotation = Angles.moveToward(rotation, targetRot, rotationSpeed * Time.delta);
        }

        @Override
        public void created() {
            sources.add(new ReflectorSource(data, rotation, x, y));
            LightBeams.get().handle(this);
        }

        @Override
        public boolean exists() {
            return isAdded();
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

        @Override
        public Unit unit(){
            //make sure stats are correct
            unit.tile(this);
            unit.team(team);
            return (Unit)unit;
        }

        @Override
        public boolean isControlled() {
            return ControlBlock.super.isControlled();
        }

        @Override
        public boolean canControl() {
            return ControlBlock.super.canControl();
        }
    }
}
