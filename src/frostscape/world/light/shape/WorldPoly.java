package frostscape.world.light.shape;

import frostscape.world.light.Lightc;
import frostscape.world.light.WorldShape;

import static frostscape.world.light.LightBeams.d4x0;
import static frostscape.world.light.LightBeams.d4y0;

public class WorldPoly implements WorldShape {
    public float x;
    public float y;
    public float[] edges, edgesOut;

    public WorldPoly(){

    }

    public WorldPoly(float x, float y, float[] edges){
        this.x = x;
        this.y = y;
        this.edges = new float[edges.length];
        for (int i = 0; i < edges.length; i++) {
            this.edges[i] = edges[i];
        }
    }

    public WorldPoly set(float x, float y, float[] edges){
        this.x = x;
        this.y = y;
        for (int i = 0; i < edges.length; i++) {
            this.edges[i] = edges[i];
        }
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
        return edges;
    }
}
