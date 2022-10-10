package frostscape.world.light;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.Damage;
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
        if(!Vars.state.isPlaying() && !shouldUpdate) return;
        shouldUpdate = false;
        lights.each(l -> {
            Seq<LightSource> sources = l.getSources();
            if(sources.size == 0) return;

            sources.each(s -> {

                s.beam.rays.clear();
                boolean enabled = true;
                int bounces = 0, maxBounces = 20;

                //Light
                while (enabled && bounces < maxBounces){
                    Polygon
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

    public static int[] d4x0 = {1, 1, 0, 0};
    public static int[] d4y0 = {0, 1, 1, 0};

    public float[] vertices(Shape2D shape){
        if(shape instanceof Rect rect){
            //Rectangles have four points
            float[] ret = new float[8];
            for (int i = 0; i < 4; i++) {
                //X ofset by width
                ret[i * 2] = d4x0[i] * rect.x + rect.width;
                //X ofset by height
                ret[i*2 + 1] = d4y0[i] * rect.y + rect.height;
            }
            return ret;
        }
    }
}
