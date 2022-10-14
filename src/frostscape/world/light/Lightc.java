package frostscape.world.light;

import arc.math.geom.Position;
import arc.math.geom.Shape2D;
import arc.struct.Seq;

import static frostscape.world.light.LightBeams.*;

public interface Lightc extends Position {
    float rotation();
    Seq<LightSource> getSources();

    //Returns reflectivity at that specific point in the object. Defaults to one.
    default float reflectivity(float x, float y){
        return 1;
    };

    LightBeams.CollisionData collision(float x, float y, float direction, ColorData color, LightBeams.CollisionData collision);

    //Hitboxes of entity. Defaults to an empty array, which means it can't be intersected.
    default WorldShape[] hitboxes() {
        return new WorldShape[0];
    }

    //Called after lights have been updated. Mostly used to apply changes after being hit by light beams
    default void afterLight(){
    }

}
