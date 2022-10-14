package frostscape.world.light;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.math.Mathh;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.graphics.Layer;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LightBeams implements SaveFileReader.CustomChunk {

    //Temparary float array passed down to LightModules when they are intersected by a light beam.s
    public static float[] positions = new float[7];

    //Falloff *per world unit* for light beams.
    public ColorData falloff = new ColorData(0.01f, 0.01f, 0.01f);
    //Change to true while game is paused and a light module is updated
    public boolean shouldUpdate = false;

    public Seq<Lightc> lights = new Seq();

    //Most of this data is used for world interactions, if I were to implement light unable to interact with the world, this would look different
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

        public ColorData(ColorData c){
            this.r = c.r;
            this.g = c.g;
            this.b = c.b;
        }

        public ColorData set(float r, float g, float b){
            this.r = r;
            this.g = g;
            this.b = b;
            return this;
        }
    }

    public interface WorldShape{
        float getX();
        float getY();
        float[] edges();
    }

    //Returns the beam's color as a Color object. Mostly used for drawing.
    public static Color toColor(ColorData col){
        return new Color(col.r, col.g, col.b);
    }

    //Handle collision with the hit light module, and add the returned collision data
    public void handleCollision(float x, float y, float rotation, int sideIndex, Seq<CollisionData> beam, Lightc module){
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

        int maxBounces = 1;
        Seq<WorldShape> shapes = new Seq<>();
        int[] intOut = new int[2];
        Vec2 pointOut = new Vec2();

        for (int i = 0; i < maxBounces; i++) {
            CollisionData first = beam.first();
            float bx1 = first.x, by1 = first.y;
            ColorData tempData = new ColorData(first.after);

            //Find the furthest point to form the end of the line segment.
            Tmp.v1.set(furthestFalloffPoint(first.after, Tmp.v1.set(bx1, by1), first.rotAfter, tempData));

            float bx2 = Tmp.v1.x, by2 = Tmp.v1.y;

            //Map the shape's indexes to their Lightc owners
            IntMap<Lightc> shapeMap = new IntMap<>();
            
            lights.each(lightc -> {
                for (int j = 0; j < lightc.hitboxes().length; j++) {
                    shapes.add(lightc.hitboxes());
                    shapeMap.put(j, lightc);
                }
            });

            shapes.sort(hitbox -> Mathf.dst2(bx1, by1, hitbox.getX(), hitbox.getY()));

            if(!linecastClosest(bx1, by1, bx2, by2, shapes, intOut, pointOut)) continue;
            handleCollision(pointOut.x, pointOut.y, Mathf.angle(bx2 - bx1, by2 - by1), intOut[0], beam, shapeMap.get(intOut[1]));

            Log.info(intOut);
            Log.info(pointOut);
        }
    }

    /**
     * Method which returns if an intersected polygon was found, and writes information to it's input fields
     * @param x1 X cord of the linecast's start
     * @param y1 Y cord of the linecast's start
     * @param x2 X cord of the linecast's end
     * @param y2 Y cord of the linecast's end
     * @param shapes The Seq of shapes to run through
     * @param dataOut An array of ints which is filled out with (In their respective order)
     *                -The index of the WorldShape found
     *                -The index of the WorldShape's side
     * @param pointOut A vector whose fields are filled out with the intersection point
     * @return
     */
    public boolean linecastClosest(float x1, float y1, float x2, float y2, Seq<WorldShape> shapes, int[] dataOut, Vec2 pointOut){

        //Set up the rects (Corner opposite to the x and y points are offset by the width and height.)
        Tmp.r1.set(x1, y1, x2 - x1, y2 - y1);

        boolean found = false;
        pointOut.set(0, 0);

        for (int i = 0; i < shapes.size; i++) {
            WorldShape shape = shapes.get(i);
            for (int j = 0; j < shape.edges().length; j += 2) {
                //Find the segment's start and end positions.
                float x3 = shape.edges()[i];
                float y3 = shape.edges()[i+1];
                float x4 = shape.edges()[i+2];
                float y4 = shape.edges()[i+3];
                Tmp.r2.set(x3, y3, x4 - x3, y4 - y3);

                //If segments don't intersect, move to next.
                if(!Tmp.r1.overlaps(Tmp.r2)) continue;

                Vec2 point = Mathh.intersection(x1, y1, x2, y2, x3, y3, x4, y4);

                //If the point found is null, or the point is further away from a previously found point, continue.
                if(point == null || (found && point.dst2(x1, y1) > pointOut.dst2(x1, y1))) continue;
                //Write to out fields
                dataOut[0] = i;
                dataOut[1] = j;
                pointOut.set(point);
                found = true;
            }
        }
        return found;
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
        color.r -= falloff.r * distance;
        color.g -= falloff.g * distance;
        color.b -= falloff.b * distance;
        return color;
    }

    //Find the furthest point away from the origin that a color will stay non-negative.
    public Vec2 furthestFalloffPoint(ColorData color, Vec2 position, float rotation, ColorData out){
        //Falloff should N E V E R. Be 0 in any of it's fields. If it is, light should be disabled.
        if(falloff.r == 0 || falloff.g == 0 || falloff.b == 0) throw new IllegalStateException("Light Falloff should never be 0.");
        float r = color.r/falloff.r, g = color.g/falloff.g, b = color.b/falloff.b;
        out.r = r;
        out.g = g;
        out.b = b;
        //Translate it by the rotation of the beam, and max length of the colours before they fall off completely, then add the original beam's position.
        return new Vec2().trns(rotation, Math.min(Math.min(r, b), g)).add(position);
    }

    public static int[] d4x0 = {1, 1, 0, 0};
    public static int[] d4y0 = {0, 1, 1, 0};

    @Deprecated
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
