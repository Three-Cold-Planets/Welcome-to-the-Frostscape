package frostscape.world.blocks.light;

import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Log;
import frostscape.Frostscape;
import frostscape.math.Mathh;
import frostscape.world.FrostscapeBlock;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.UpgradesType;
import frostscape.world.light.LightBeams;
import frostscape.world.light.WorldShape;
import frostscape.world.light.Lightc;
import frostscape.world.light.shape.WorldRect;

import static mindustry.Vars.tilesize;

public class ReflectiveWall extends FrostscapeBlock {

    public static WorldShape[] tmp = new WorldShape[1];
    public ReflectiveWall(String name) {
        super(name);
    }

    public class ReflectiveWallBuild extends FrostscapeBuilding implements Lightc {

        public WorldRect hitbox = new WorldRect();
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
            return added;
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

            if(flipX) newRot = Mathh.rotReflectionX(rotation);
            if(flipY) newRot = Mathh.rotReflectionY(rotation);

            collision.rotAfter = newRot;

            return collision;
        }
    }
}
