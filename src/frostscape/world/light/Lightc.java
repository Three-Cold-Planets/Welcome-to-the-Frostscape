package frostscape.world.light;

import arc.math.geom.Position;
import arc.math.geom.Shape2D;
import arc.struct.Seq;

public interface Lightc extends Position {
    float rotation();
    Seq<LightBeams.LightSource> getSources();

    //Returns reflectivity at that specific point in the object. Defaults to one.
    default float reflectivity(float x, float y){
        return 1;
    };

    LightBeams.CollisionData collision(float x, float y, float direction, LightBeams.ColorData color, LightBeams.CollisionData collision);

    //Hitboxes of entity. Defaults to an empty array, which means it can't be intersected.
    default Shape2D[] hitboxes() {
        return new Shape2D[0];
    }

    //Called after lights have been updated. Mostly used to apply changes after being hit by light beams
    default void afterLight(){
    }

}
