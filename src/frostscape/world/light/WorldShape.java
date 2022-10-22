package frostscape.world.light;

/**
    A shape specifically made to work with a Lightc
 */
public interface WorldShape{
    float getX();
    float getY();
    float[] edges();
}