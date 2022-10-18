package frostscape.math;

import arc.Core;
import mindustry.Vars;

public class Math3D {
    /**
     * Originally by
     * @author MEEP
     * Modified by
     * @author Sh1penfire
     * */

    //Ty meep, didn't realize how simple it was

    public static float xCamOffset2D(float y, float height){
        return xCamOffset2D(y, height, 1 + 7 * (1 - Math.max(0, Core.settings.getInt("frostscape-parallax")/100)));
    }

    public static float yCamOffset2D(float y, float height){
        return yCamOffset2D(y, height, 1 + 7 * (1 - Math.max(0, Core.settings.getInt("frostscape-parallax")/100)));
    }
    public static float xCamOffset2D(float x, float height, float spacing){
        return (x - Core.camera.position.x) * height/spacing;
    }

    public static float yCamOffset2D(float y, float height, float spacing){
        return (y - Core.camera.position.y) * height/spacing;
    }

    //I'd love to explain how this works but just check desmos
    public static float rotReflectionX(float rotation){
        return Math.abs(360 + 180 - rotation) % 360;
    }
    public static float rotReflectionY(float rotation){
        return Math.abs(360 + 180 - rotation + 90) % 360 + 90;
    }

    public static class HeightHolder{
        public float height;
        public float lift;

        public HeightHolder(float height, float lift){
            this.height = height;
            this.lift = lift;
        }
    }
}
