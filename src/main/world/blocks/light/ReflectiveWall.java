package main.world.blocks.light;

import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.io.Writes;
import main.math.MathUtils;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import main.world.UpgradesType;
import main.world.systems.light.LightBeams;
import main.world.systems.light.Lightc;
import main.world.systems.light.WorldShape;
import main.world.systems.light.shape.WorldRect;

import static mindustry.Vars.tilesize;

public class ReflectiveWall extends BaseBlock {

    public static WorldShape[] tmp = new WorldShape[1];
    public ReflectiveWall(String name) {
        super(name);
    }

    public class ReflectiveWallBuild extends BaseBuilding implements Lightc {

        public WorldRect hitbox = new WorldRect();

        @Override
        public void created() {
            LightBeams.get().handle(this);
        }

        @Override
        public boolean exists() {
            return isAdded();
        }

        @Override
        public boolean collides() {
            return true;
        }

        @Override
        public void hitbox(Rect out) {
            super.hitbox(out);
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
            sequence.add(hitbox.set(x - size * tilesize/2, y - size * tilesize/2, size * tilesize, size * tilesize));
        }
        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            float newRot = rotation;

            boolean flipX = false, flipY = true;

            if((side/2 % 2) == 0) {
                flipX = true;
                flipY = false;
            };

            if(flipX) newRot = MathUtils.rotReflectionX(rotation);
            if(flipY) newRot = MathUtils.rotReflectionY(rotation);

            collision.rotAfter = newRot;

            return collision;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
        }
    }
}
