package main.util;

import arc.Core;
import arc.math.geom.Vec2;
import main.math.Math3D;

public class DrawUtils {
    public static Vec2 tv;
    /*This conssiders
    -X and Y of position
    -Wind
    -Parallax
     */

    //Weights for different materials
    public static float
        sparkWeight = 1,
        smokeWeight = 0.05f,
        heavySmokeWeight = 0.15f;

    //Height gain/second for different materials
    public static float
        smokeDrift = 0.75f/60;

    public static void speckOffset(float x, float y, float height, float magnitude, float weight, Vec2 out){
        Vec2 w = new Vec2();
        windOffset(magnitude, weight, w);
        w.add(x,y);
        parallaxOffset(w.x, w.y, height, out);
    }

    public static void windOffset(float magnitude, float weight, Vec2 out){
        out.set(WeatherUtils.windDirection());
        out.scl(magnitude * Core.settings.getInt("frostscape-wind-visual-force")/100, magnitude * Core.settings.getInt("frostscape-wind-visual-force")/100);
        float length = Math.max(out.len() - weight, 0);
        out.setLength(length);
    }

    public static void parallaxOffset(float x, float y, float height, Vec2 out){
        float px = Math3D.xCamOffset2D(x, height), py = Math3D.yCamOffset2D(y, height);
        out.set(x + px, y + py);
    }
}
