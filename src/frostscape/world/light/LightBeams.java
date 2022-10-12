package frostscape.world.light;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.graphics.Layer;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LightBeams implements SaveFileReader.CustomChunk {

    //Falloff per world unit for light beams.
    public ColorData falloff = new ColorData(0.01f, 0.01f, 0.01f);
    //Change to true while game is paused and a light module is updated
    public boolean shouldUpdate = false;

    public Seq<Lightc> lights = new Seq();

    //Most of this data is used for drawing the light beam, if I were to implement light with no visuals and unable to interact with the world, this would look different
    public static class LightSource{
        //The position is offset from main module instead of representing coordinates in the world.
        public float x, y;
        public ColorData color;
        public float rotation;
        public boolean emitting = true;

        public Seq<CollisionData> beam = new Seq<>();

        public LightSource(float x, float y, ColorData color, float rotation){
            this.x = x;
            this.y = y;
            this.color = color;
            this.rotation = rotation;
        }
    }

    public class LightBeam{
        public Seq<CollisionData> rays = new Seq<>();
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

    public static class ColorData{
        public float r;
        public float g;
        public float b;

        public ColorData(float r, float g, float b){
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    //Returns the beam's color as a Color object. Mostly used for drawing.
    public static Color toColor(ColorData col){
        return new Color(col.r, col.g, col.b);
    }

    //Handle collision with the hit light module, and add the returned collision data
    public void handleCollision(float x, float y, float rotation, Seq<CollisionData> beam, Lightc module){
        CollisionData last = beam.first();
        //This statement should never be reached if the falloff will make it reach 0, 0, 0
        ColorData color = applyFalloff(last.after, Mathf.dst(last.x, last.y, x, y));

        beam.add(
            module.collision(x, y, rotation, color, new CollisionData(x, y, last.rotAfter, color))
        );
    }

    public void updateBeams(){
        if(!Vars.state.isPlaying() && !shouldUpdate) return;
        shouldUpdate = false;
        Log.info("Start");
        lights.each(l -> {
            Seq<LightSource> sources = l.getSources();
            if(sources.size == 0) return;

            sources.each(s -> {
                if(!s.emitting) return;
                updateSource(l, s);
            });
            l.afterLight();
        });
    }

    //Left separate in the case that a light source needs to update itself
    public void updateSource(Lightc l, LightSource s){

        Seq<CollisionData> beam = s.beam;
        beam.clear();

        float x = l.getX() + Tmp.v1.trns(l.rotation(), s.x, s.y).x;
        float y = l.getY() + Tmp.v1.y;
        Log.info(x + ", " + y);

        //Add collision data at the start of the beam
        CollisionData last = new CollisionData(x, y, s.rotation, s.color){{
            rotAfter = rotBefore;
            after = before;
        }};
        beam.add(last);

        //Main loop for testing collisions

        //Disable if light falloff is too much
        boolean enabled = true;
        int bounces = 0, maxBounces = 20;
        while (enabled && bounces < maxBounces){
            enabled = false;
        }

    }

    public void draw(){
        Draw.z(Layer.light + 5);
        lights.each(l -> {
            l.getSources().each(source -> {
                Seq<CollisionData> beams = source.beam;
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
        return new float[]{
                0, 0,
                0, 0,
                0, 0,
                0, 0
        };
    }


    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.f(falloff.r);
        write.f(falloff.b);
        write.f(falloff.g);
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        Reads read = new Reads(dataInput);
        falloff.r = read.f();
        falloff.b = read.f();
        falloff.g = read.f();
    }

    @Override
    public boolean shouldWrite() {
        return SaveFileReader.CustomChunk.super.shouldWrite();
    }

}
