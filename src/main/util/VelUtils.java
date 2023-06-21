package main.util;

import arc.math.Angles;
import arc.math.geom.Vec2;

public class VelUtils {
    public static float direction(Vec2 velocity, float direction){
        return 1 - Math.min(Angles.forwardDistance(velocity.angle(), direction)
                , Angles.backwardDistance(velocity.angle(), direction))/360;
    }
}
