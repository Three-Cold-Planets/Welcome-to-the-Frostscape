package frostscape.world.light.shape;


import frostscape.world.light.WorldShape;

import static frostscape.world.light.LightBeams.d4x0;
import static frostscape.world.light.LightBeams.d4y0;

public class WorldRect implements WorldShape {
    public float x;
    public float y;
    public float width;
    public float height;

    public WorldRect(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float[] edges() {
        float[] ret = new float[8];
        for (int i = 0; i < 4; i++) {
            //X ofset by width
            ret[i * 2] = d4x0[i] * x + width;
            //X ofset by height
            ret[i*2 + 1] = d4y0[i] * y + height;
        }
        return ret;
    }
}