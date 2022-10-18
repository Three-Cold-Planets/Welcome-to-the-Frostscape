package frostscape.world.light;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.FloatSeq;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.math.Mathh;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.bullet.RailBulletType;
import mindustry.graphics.Layer;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//Todo: Allow for light beams to branch off in a tree-like system.
public class LightBeams implements SaveFileReader.CustomChunk {

    //Falloff *per world unit* for light beams.
    public ColorData falloff = new ColorData(0.01f, 0.01f, 0.01f);
    //Change to true while game is paused and a light source is updated
    public boolean shouldUpdate = false;

    public Seq<Lightc> lights = new Seq<>();

    //Most of this data is used for world interactions, if I were to implement light unable to interact with the world, this would look different
    public static abstract class LightSource implements Position{
        public ColorData color;
        public float rotation;
        public boolean emitting = true;

        public Seq<CollisionData> beam = new Seq<>();

        public LightSource(ColorData color, float rotation){
            this.color = color;
            this.rotation = rotation;
        }
    }

    public static class CollisionData{
        //To be used
        public CollisionData(float x, float y, float rotation, ColorData color){
            this.x = x;
            this.y = y;
            this.rotBefore = this.rotAfter = rotation;
            this.before = this.after = color;
            collision = false;
        }

        public CollisionData(float x, float y, float rotBefore, float rotAfter, ColorData before, ColorData after, boolean collision){
            this.x = x;
            this.y = y;
            this.rotBefore = rotBefore;
            this.rotAfter = rotAfter;
            this.before = before;
            this.after = after;
            this.collision = collision;
        }
        public ColorData before, after;
        public float x, y;
        public float rotBefore, rotAfter;
        //Whether this is an actual "Collision" or a change of state in the light beam. Used exclusively for rendering.
        public boolean collision;
    }

    public static class ColorData{
        public float r;
        public float g;
        public float b;

        public ColorData(){
            r = b = g = 1;
        }

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

        @Override
        public String toString() {
            return "ColorData{" +
                    "r=" + r +
                    ", g=" + g +
                    ", b=" + b +
                    '}';
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

    //Handles adding CollisionData instances for each point a color in the beam hits zero.
    public void handleFalloffPoints(Vec2 closest, Vec2 furthest, float rotation, Seq<CollisionData> beam, CollisionData last, ColorData closestCol){
        //Find the color at the falloff point

        //Add collision data for the closest point.
        beam.add(new CollisionData(closest.x, closest.y, rotation, closestCol));

        ColorData newCol = new ColorData();
        //Find the new closest point
        Vec2 newClosest = closestFalloffPoint(closestCol, closest, rotation, newCol);

        Log.info("x: " + last.x + ", " + "y: " + last.y);
        Log.info(closest);
        Log.info(newClosest);
        Log.info(furthest);

        //One of the colours has already faded, so if the point isn't the same as the ending that means there is only one remaining
        if(newClosest.equals(furthest)) return;
        beam.add(new CollisionData(newClosest.x, newClosest.y, rotation, applyFalloff(newCol, newClosest.dst(furthest))));

        //After this, only one colour should remain (Or two if new closest equals furthest)
    }

    //Handle collision with the hit light module, and add the returned collision data
    public void handleCollision(float x, float y, float rotation, int shape, int side, Seq<CollisionData> beam, Lightc module){
        //Get last collision point
        CollisionData last = beam.get(beam.size - 1);
        //Apply falloff to the light beam.
        ColorData color = applyFalloff(last.after, Mathf.dst(last.x, last.y, x, y));

        //Add the returned collision data to the beam
        beam.add(
            module.collision(x, y, rotation, shape, side, color, new CollisionData(x, y, last.rotAfter, color))
        );
    }

    public void updateBeams(){
        //Todo: Finish main loop
        if(true) return;
        if(!Vars.state.isPlaying() && !shouldUpdate) return;
        shouldUpdate = false;
        lights.each(l -> {
            Seq<LightSource> sources = l.getSources();
            if(sources.size == 0) return;

            sources.each(s -> {
                if(!s.emitting) {
                    s.beam.clear();
                    return;
                }
                updateSource(l, s);
            });
            l.afterLight();
        });
    }

    //Left separate in the case that a light source needs to update itself
    public void updateSource(Lightc l, LightSource s){

        //Clear previous collision points
        Seq<CollisionData> beam = s.beam;
        beam.clear();

        float x = s.getX();
        float y = s.getY();
        Log.info(x + ", " + y);

        //Add collision data at the start of the beam. This technically isn't a "Collision" but you can blame it on bad naming I suppose.
        CollisionData start = new CollisionData(x, y, s.rotation, s.color);
        beam.add(start);

        //Main loop for testing collisions

        //If light beams continued forever I'd be dammed
        int maxBounces = 1;

        Seq<WorldShape> shapes = new Seq<>();
        int[] intOut = new int[2];
        Vec2 pointOut = new Vec2();
        //Remember last collision point
        CollisionData last = start;

        for (int i = 0; i < maxBounces; i++) {
            float bx1 = last.x, by1 = last.y;
            float rotation = last.rotAfter;
            ColorData tempData = new ColorData(last.after);

            //Find the furthest point to form the end of the line segment.
            Vec2 furthestFade = (furthestFalloffPoint(last.after, Tmp.v1.set(bx1, by1), rotation)), end = furthestFade;

            //Asign variables to the ending cords of the beam
            float bx2 = furthestFade.x, by2 = furthestFade.y;

            //Map the shape's indexes to their Lightc owners
            IntMap<Lightc> shapeMap = new IntMap<>();
            
            lights.each(lightc -> {
                for (int j = 0; j < lightc.hitboxes().length; j++) {
                    shapes.add(lightc.hitboxes());
                    shapeMap.put(j, lightc);
                }
            });

            shapes.sort(hitbox -> Mathf.dst2(bx1, by1, hitbox.getX(), hitbox.getY()));

            //Check for if the beam intersects anything. If it does, set a new target position and handle falloff.
            boolean hitTarget = linecastClosest(bx1, by1, bx2, by2, shapes, intOut, pointOut);
            if(hitTarget){
                end.set(pointOut);

                //If the beam's closest fading point is reached before bouncing, handle that first
                Vec2 closestFade = closestFalloffPoint(last.after, Tmp.v1.set(bx1, by1), rotation, tempData);
                if(closestFade.dst(bx1, by1) < end.dst(bx1, by1)){
                    handleFalloffPoints(closestFade, end, rotation, beam, last, tempData);
                }

                handleCollision(pointOut.x, pointOut.y, Mathf.angle(bx2 - bx1, by2 - by1), intOut[0], intOut[1], beam, shapeMap.get(intOut[0]));

                //End it here, if the condition for the if statement was not met, use the logic after this.
                continue;
            }

            //Handle falloff unless falloff is equal
            Vec2 closestFade = closestFalloffPoint(last.after, Tmp.v1.set(bx1, by1), rotation, tempData);
            if(closestFade.dst(bx1, by1) < end.dst(bx1, by1)){
                handleFalloffPoints(closestFade, end, rotation, beam, last, tempData);
            }

            //Add ending point where beam fades out
            beam.add(new CollisionData(end.x, end.y, rotation, new ColorData(0, 0, 0)));
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
                    Draw.blend();
                    Lines.line(before.x, before.y, after.x, after.y);
                }
            });
        });
    }

    public ColorData applyFalloff(ColorData color, float distance){
        color.r = Mathf.maxZero(color.r - falloff.r * distance);
        color.g = Mathf.maxZero(color.g - falloff.g * distance);
        color.b = Mathf.maxZero(color.b - falloff.b * distance);
        return color;
    }

    //Find the furthest point away from the origin before the light fades out of existience.
    public Vec2 furthestFalloffPoint(ColorData color, Vec2 position, float rotation){
        //Falloff should N E V E R. Be 0 in any of it's fields. If it is, light should be disabled.
        if(falloff.r == 0 || falloff.g == 0 || falloff.b == 0) throw new IllegalStateException("Light Falloff should never be 0.");
        //Find max distances before fading
        float r = color.r/falloff.r, g = color.g/falloff.g, b = color.b/falloff.b;

        //Translate it by the rotation of the beam, and max length of the colours before they fall off completely, then add the original beam's position.
        return new Vec2().trns(rotation, Math.max(Math.max(r, b), g)).add(position);
    }

    /**Find the closest point away from the origin that a colour will fade to 0. Use {@link frostscape.world.light.LightBeams#furthestFalloffPoint} instead if finding furthest point.
     */
    public Vec2 closestFalloffPoint(ColorData color, Vec2 position, float rotation, ColorData out){
        //Falloff should N E V E R. Be 0 in any of it's fields. If it is, light should be disabled.
        if(falloff.r == 0 || falloff.g == 0 || falloff.b == 0) throw new IllegalStateException("Light Falloff should never be 0.");

        //Kind of a hacky solution, but it works ig. Stores color data in the first half and the falloff values in the second to avoid allocating memory for two separate arrays.
        float[] colors = new float[]{color.r, color.g, color.b, falloff.r, falloff.b, falloff.g};
        float distance = 0;
        boolean found = false;
        //Find the max distance away from the start the beam can go, ignoring components already zero
        for (int i = 0; i < colors.length/2; i++) {
            float comp = colors[i];
            float dst = comp/colors[i + 3];
            if(comp != 0 && (!found || dst < distance)) distance = dst;
        }

        //Apply falloff and write it to out
        applyFalloff(out.set(color.r, color.g, color.b), distance);

        //Translate it by the rotation of the beam, and max length of the colours before they fall off completely, then add the original beam's position.
        return new Vec2().trns(rotation, distance).add(position);
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
