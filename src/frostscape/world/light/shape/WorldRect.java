package frostscape.world.light.shape;


import arc.graphics.g2d.Fill;
import frostscape.world.light.WorldShape;

import static frostscape.world.light.LightBeams.d4x0;
import static frostscape.world.light.LightBeams.d4y0;

public class WorldRect implements WorldShape {
    public float x;
    public float y;
    public float width;
    public float height;

    public float[] edges = new float[8];

    public WorldRect(){

    }
    public WorldRect(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public WorldRect set(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
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
        for (int i = 0; i < 4; i++) {
            //X ofset by width
            edges[i * 2] = x + width * d4x0[i];
            //X ofset by height
            edges[i*2 + 1] = y + height* d4y0[i];
        }
        return edges;
    }
}