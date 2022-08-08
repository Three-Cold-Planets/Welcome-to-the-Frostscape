package frostscape.math;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Tmp;

public class Mathh {
    public static Vec2 moveToward(Vec2 a, Vec2 b, float speed){
        float distance = a.dst(b);
        float newDst = Mathf.approach(distance, 0, speed);
        return a.sub(b).clamp(0, newDst).add(b);
    }
}
