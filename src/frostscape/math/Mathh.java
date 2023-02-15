package frostscape.math;

import arc.math.Angles;
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

    //Returns the point at which two lines intersect, or null if not valid
    public static Vec2 intersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){

        float det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4))/det;
        float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3))/det;

        if (t < 0 || t > 1 || u < 0 || u > 1) {
            //Lines are parallel (somehow)
            return null;
        } else {
            return new Vec2(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
        }
    }

    //Returns the point at which two lines intersect, assuming the two lines intersect
    public static Vec2 intersectionUnsafe(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){

        float det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4))/det;

        return new Vec2(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
    }

    //I'd love to explain how this works but just check desmos
    public static float rotReflectionX(float rotation){
        return Math.abs(360 + 180 - rotation);
    }

    public static float rotReflectionY(float rotation){
        return Math.abs(360 + 180 - rotation + 90) % 360 + 90;
    }

    public static float rotReflection(float rotation, float angle){
        float reflection = rotation;
        reflection = 360 + rotation - (rotation - angle) * 2 + 180;
        return reflection;
    }
}
