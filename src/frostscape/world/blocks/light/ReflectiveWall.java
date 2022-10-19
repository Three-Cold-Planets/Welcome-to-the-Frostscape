package frostscape.world.blocks.light;

import arc.math.geom.Rect;
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

    public ReflectiveWall(String name) {
        super(name);
    }

    public class ReflectiveWallBuild extends FrostscapeBuilding implements Lightc {
        @Override
        public void created() {
            Frostscape.lights.handle(this);
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
        public WorldShape[] hitboxes(){
            return new WorldShape[]{new WorldRect(x - size * tilesize/2, y - size * tilesize/2, size * tilesize, size * tilesize)};
        }
        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            float newRot = rotation;

            float difX = Math.abs(this.x - x);
            float difY = Math.abs(this.y - y);

            boolean flipX = false;
            boolean flipY = true;
            if(difX > difY) {
                flipX = true;
                flipY = false;
            };

            if(flipX) newRot = Mathh.rotReflectionX(rotation);
            if(flipY) newRot = Mathh.rotReflectionY(rotation);

            return new LightBeams.CollisionData(x, y, newRot, new LightBeams.ColorData(color));
        }
    }
}
