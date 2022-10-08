package frostscape.world.light;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.graphics.Layer;

import java.awt.geom.Point2D;

public class LightBeams {
    //Change to true while game is paused and a light module is updated
    public boolean shouldUpdate = false;

    public static Seq<LightModule> lights = new Seq();

    //Most of this data is used for drawing the light beam, if I were to implement light with no visuals and unable to interact with the world, this would look different
    public class LightSource{
        public ColorData color;
        public float rotation;

        public LightBeam beam;
    }

    public class LightBeam{
        public Seq<CollisionData> rays;
    }

    public class CollisionData{
        public CollisionData(float x, float y, float rotBefore, ColorData before){
            this.x = x;
            this.y = y;
            this.rotBefore = rotBefore;
            this.before = before;
        };
        public ColorData before, after;
        public float x, y;
        public float rotBefore, rotAfter;
    }

    public class ColorData{
        public float r, g, b;
    }

    public static Color toColor(ColorData col){
        return new Color(col.r, col.g, col.b);
    }

    public void handleCollision(float x, float y, float rotation, LightBeam beam, LightModule module){
        CollisionData last = beam.rays.first();
        //This statement should never be reached if the falloff will make it reach 0, 0, 0
        ColorData color = applyFalloff(last.after, Mathf.dst(last.x, last.y, x, y));

        beam.rays.add(
                module.collision(x, y, rotation, color, new CollisionData(x, y, last.rotAfter, color))
        );
    }

    public void updateBeams(){
        lights.each(l -> {
            Seq<LightSource> sources = l.getSources();
            if(sources.size == 0) return;

            sources.each(s -> {

                s.beam.rays.clear();
                boolean enabled = true;
                int bounces = 0, maxBounces = 20;

                while (enabled && bounces < maxBounces){

                }
            });
        });
    }

    public void draw(){
        Draw.z(Layer.light + 5);
        lights.each(l -> {
            l.getSources().each(source -> {
                Seq<CollisionData> beams = source.beam.rays;
                for (int i = 0; i < beams.size - 1; i++) {
                    CollisionData before = beams.get(i), after = beams.get(i + 1);
                    Lines.line(before.x, before.y, after.x, after.y);
                }
            });
        });
    }

    public ColorData applyFalloff(ColorData color, float distance){
        //
        return color;
    }
}
