package frostscape.world.light;

import arc.math.geom.Position;
import arc.struct.Seq;

import static frostscape.world.light.LightBeams.*;

public interface Lightc extends Position {

    boolean exists();

    default boolean collides(){
        return false;
    }
    Seq<LightSource> empty = Seq.with();

    default void getSources(Seq<LightSource> out) {

    }

    //Returns reflectivity at that specific point in the object. Defaults to one.
    default float reflectivity(int shape, int side){
        return 1;
    };

    LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, ColorData color, LightBeams.CollisionData collision);

    //Hitboxes of entity. Defaults to an empty array, which means it can't be intersected. The array should likely be a static array filled with elements to avoid unescecary alocation.
    default void hitboxes(Seq<WorldShape> seq) {

    }

    //Called after lights have been updated. Mostly used to apply changes after being hit by light beams
    default void afterLight(){
    }

}
