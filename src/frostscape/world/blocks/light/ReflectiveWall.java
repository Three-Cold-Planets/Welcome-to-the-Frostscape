package frostscape.world.blocks.light;

import arc.struct.Seq;
import frostscape.world.FrostscapeBlock;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.UpgradesType;
import frostscape.world.light.LightBeams;
import frostscape.world.light.Lightc;

public class ReflectiveWall extends FrostscapeBlock {

    public ReflectiveWall(String name) {
        super(name);
        update = false;
    }

    public class ReflectiveWallBuild extends FrostscapeBuilding implements Lightc {

        @Override
        public UpgradesType type() {
            return (UpgradesType) block;
        }
        @Override
        public float reflectivity(int shape, int side) {
            return 1;
        }

        @Override
        public LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, LightBeams.ColorData color, LightBeams.CollisionData collision) {
            float difX = Math.abs(this.x - x);
            float difY = Math.abs(this.y - y);

            boolean flipX = false;
            boolean flipY = true;
            if(difX > difY) {
                flipX = true;
                flipY = false;
            }

            float transX =

            return new LightBeams.CollisionData();
        }

        @Override
        public LightBeams.WorldShape[] hitboxes() {
            return Lightc.super.hitboxes();
        }
    }
}
