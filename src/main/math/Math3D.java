package main.math;

import arc.Core;

public class Math3D {

    public static float parallax;
    /**
     * Originally by
     * @author MEEP
     * Modified by
     * @author Sh1penfire
     * */

    //Ty meep, didn't realize how simple it was

    public static float xCamOffset2D(float x, float height){
        return xCamOffset2D(x, height, 1 + (1 - Math.max(0, parallax)));
    }

    public static float yCamOffset2D(float y, float height){
        return yCamOffset2D(y, height, 1 + 7 * (1 - Math.max(0, parallax)));
    }
    public static float xCamOffset2D(float x, float height, float spacing){
        return (x - Core.camera.position.x) * height/spacing;
    }

    public static float yCamOffset2D(float y, float height, float spacing){
        return (y - Core.camera.position.y) * height/spacing;
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
