package frostscape.world.light;

import arc.math.geom.Shape2D;
import arc.struct.Seq;

public interface LightModule {
    Seq<LightBeams.LightSource> getSources();

    float reflectivity(float x, float y);

    LightBeams.CollisionData collision(float x, float y, float direction, LightBeams.ColorData color, LightBeams.CollisionData collision);

    Shape2D[] hitboxes();
}
