package frostscape.world.light;

import arc.math.geom.Position;
import arc.math.geom.Shape2D;
import arc.struct.Seq;
import mindustry.gen.Rotc;

import static frostscape.world.light.LightBeams.*;

public interface Lightc extends Position {

    //Haha  exists
    boolean exists();
    Seq<LightSource> empty = Seq.with();
    default Seq<LightSource> getSources(){return empty;};

    //Returns reflectivity at that specific point in the object. Defaults to one.
    default float reflectivity(int shape, int side){
        return 1;
    };

    LightBeams.CollisionData collision(float x, float y, float rotation, int shape, int side, ColorData color, LightBeams.CollisionData collision);

    //Hitboxes of entity. Defaults to an empty array, which means it can't be intersected.
    default WorldShape[] hitboxes() {
        return new WorldShape[0];
    }

    //Called after lights have been updated. Mostly used to apply changes after being hit by light beams
    default void afterLight(){
    }

}
