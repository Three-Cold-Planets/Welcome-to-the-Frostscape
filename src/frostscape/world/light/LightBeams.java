package frostscape.world.light;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
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
import frostscape.content.Palf;
import frostscape.graphics.FrostShaders;
import frostscape.math.Mathh;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//Todo: Allow for light beams to branch off in a tree-like system.
public class LightBeams implements SaveFileReader.CustomChunk {

    //Falloff *per world unit* for light beams.
    public ColorData falloff = new ColorData(0.02f, 0.02f, 0.02f);
    //Change to true while game is paused and a light source is updated
    public boolean shouldUpdate = false;

    public Seq<Lightc> lights = new Seq<>(), removeable = new Seq<>();
    public Seq<LightSource> out = new Seq<>();
    public Seq<WorldShape> tmps = new Seq<WorldShape>(), shapes = new Seq<WorldShape>();

    public ObjectMap<WorldShape, Lightc> shapeMap = new ObjectMap<>();
    public static float[] tmpf = new float[6];

    public static int[] intOut = new int[2];

    public static float whiteIntensity = 10, alphaIntensity = 5;

    public static Rect r1 = new Rect(), r2 = new Rect();

    public static Color[] colors = new Color[]{
            Palf.lightRed, Palf.lightGreen, Palf.lightBlue, Palf.darkRed, Palf.darkGreen, Palf.darkBlue
    };

    //Most of this data is used for world interactions, if I were to implement light unable to interact with the world, this would look different
    public static abstract class LightSource implements Position{
        public ColorData color;
        public float rotation;
        public boolean emitting = true;

        public float beamWidth = 1;

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

    //Returns the beam's color as a Color object. Mostly used for drawing.
    public static Color toColor(ColorData col){
        //In the case that all elements are 0, make the light black to make it fade properly
        if(col.r + col.g + col.b == 0) {
            return new Color(0, 0, 0, 0);
        }

        Color returnCol = new Color(1, 1, 1);

        float total = 0, colorCount = 0;
        tmpf[0] = col.r;
        tmpf[1] = col.g;
        tmpf[2] = col.b;


        //Out of the three colors, the ones which are 0 are not added to the array

        for (int i = 0; i < tmpf.length; i++) {
            float component = tmpf[i];
            //If the current color is 0, ignore it
            if(component <= 0) continue;

            //The first three elements are the colors, handle them appropriately
            if(i < 3){
                tmpf[i + 3] = component;
                total += component;
                colorCount++;
                continue;
            }

            //Make this a percentage of the whole

            float percentage = component / total * colorCount;
            tmpf[i] = percentage;
            /*
            Tmp.c1.set(colors[i - 3]).lerp(colors[i], component/whiteIntensity).mul(percentage);
            Log.info(Tmp.c1.r + ", " + Tmp.c1.g + ", " + Tmp.c1.g);

             */

            returnCol.add(Tmp.c1.set(colors[i - 3]).mul(percentage));
        }

        returnCol.a(total/colorCount/alphaIntensity);

        return returnCol.set(tmpf[0], tmpf[1], tmpf[2]);
    }

    public void handle(Lightc comp){
        lights.add(comp);
    }

    //Handles adding CollisionData instances for each point a color in the beam hits zero.
    public void handleFalloffPoints(Vec2 closest, Vec2 furthest, float rotation, Seq<CollisionData> beam, CollisionData last, ColorData closestCol){
        //Find the color at the falloff point

        //Add collision data for the closest point.
        beam.add(new CollisionData(closest.x, closest.y, rotation, closestCol));

        ColorData newCol = new ColorData();
        //Find the new closest point
        Vec2 newClosest = closestFalloffPoint(closestCol, closest, rotation, newCol);

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
        ColorData color = applyFalloff(new ColorData(last.after), Mathf.dst(last.x, last.y, x, y));

        //Add the returned collision data to the beam

        CollisionData returned = module.collision(x, y, rotation, shape, side, color, new CollisionData(x, y, last.rotAfter, last.rotAfter, color, new ColorData(color), true));

        beam.add(
                returned
        );
    }

    public void updateBeams(){
        //Todo: Finish main loop
        if(!Vars.state.isPlaying() && !shouldUpdate) return;
        shouldUpdate = false;

        //Map the shapes to their Lightc owners
        shapeMap.clear();

        lights.each(lightc -> {
            if(!lightc.collides()) return;
            lightc.hitboxes(tmps);
            for (int j = 0; j < tmps.size; j++) {
                shapes.add(tmps.get(j));
                shapeMap.put(tmps.get(j), lightc);
            }
            tmps.clear();
        });

        lights.each(l -> {
            if(!l.exists()) removeable.add(l);
            out.clear();
            l.getSources(out);
            if(out.size == 0) return;

            out.each(s -> {
                if(!s.emitting) {
                    s.beam.clear();
                    return;
                }
                updateSource(l, s);
            });
            l.afterLight();
        });

        //WELCOME TO THE UNEXIST
        removeable.each(r -> lights.remove(r));
        removeable.clear();
    }

    //Left separate in the case that a light source needs to update itself
    public void updateSource(Lightc l, LightSource s){

        //Clear previous collision points
        Seq<CollisionData> beam = s.beam;
        beam.clear();

        float x = s.getX();
        float y = s.getY();

        //Add collision data at the start of the beam. This technically isn't a "Collision" but you can blame it on bad naming I suppose.
        CollisionData start = new CollisionData(x, y, s.rotation, new ColorData(s.color));
        beam.add(start);

        //Main loop for testing collisions

        //If light beams continued forever I'd be dammed
        int bounceCap = 6;

        Vec2 pointOut = new Vec2();
        //Remember last collision point
        CollisionData last = start;

        for (int i = 0; i < bounceCap; i++) {

            if(Mathf.zero(Math.abs(last.after.r) + Math.abs(last.after.g) + Math.abs(last.after.b))) break;
            float bx1 = last.x, by1 = last.y;
            float rotation = last.rotAfter;
            ColorData tempData = new ColorData(last.after);

            //Find the furthest point to form the end of the line segment.
            Vec2 furthestFade = (furthestFalloffPoint(last.after, Tmp.v1.set(bx1, by1), rotation)), end = furthestFade;

            //Asign variables to the ending cords of the beam
            float bx2 = furthestFade.x, by2 = furthestFade.y;

            //Check for if the beam intersects anything. If it does, set a new target position and handle falloff.
            boolean hitTarget = linecastClosest(bx1, by1, bx2, by2, shapes, intOut, pointOut);
            if(hitTarget){
                end.set(pointOut);

                //If the beam's closest fading point is reached before bouncing, handle that first
                Vec2 closestFade = closestFalloffPoint(last.after, Tmp.v1.set(bx1, by1), rotation, tempData);
                if(closestFade.dst(bx1, by1) < end.dst(bx1, by1)){
                    handleFalloffPoints(closestFade, end, rotation, beam, last, tempData);
                }

                handleCollision(pointOut.x, pointOut.y, Mathf.angle(bx2 - bx1, by2 - by1), intOut[0], intOut[1], beam, shapeMap.get(shapes.get(intOut[0])));

                last = beam.get(beam.size - 1);
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
            //Break out of the loop
            break;
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
        r1.set(x1, y1, x2 - x1, y2 - y1).normalize();;

        boolean found = false;
        pointOut.set(0, 0);

        for (int i = 0; i < shapes.size; i++) {
            WorldShape shape = shapes.get(i);
            int size = shape.edges().length;
            for (int j = 0; j < size; j += 2) {
                //Find the segment's start and end positions.
                float x3 = shape.getX() + shape.edges()[(j) % size];
                float y3 = shape.getY() + shape.edges()[(j+1) % size];
                float x4 = shape.getX() + shape.edges()[(j+2) % size];
                float y4 = shape.getY() + shape.edges()[(j+3) % size];
                r2.set(x3, y3, x4 - x3, y4 - y3).normalize();

                if(!r1.overlaps(r2)) continue;
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
        this.draw(false);
    }

    public void draw(boolean debug){
        Draw.mixcol();
        Draw.blend();
        FrostShaders.effectBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.draw(Layer.light + 5, () -> {
            FrostShaders.effectBuffer.begin(Color.clear);
            lights.each(l -> {
                out.clear();
                l.getSources(out);
                out.each(source -> {
                    if(!source.emitting || source.beam.size == 0) return;

                    Seq<CollisionData> beams = source.beam;
                    for (int i = 0; i < beams.size - 1; i++) {
                        Draw.color();
                        CollisionData before = beams.get(i), after = beams.get(i + 1);
                        Color start = toColor(before.after), end = toColor(after.before);
                        //if(debug) Log.info(start.r + ", " + start.g + ", " + start.b);

                        Tmp.v1.trns(before.rotAfter, source.beamWidth).rotate(90);
                        float x1 = before.x + Tmp.v1.x, y1 = before.y + Tmp.v1.y, x2 = before.x - Tmp.v1.x, y2 = before.y - Tmp.v1.y;
                        Tmp.v1.trns(before.rotAfter, source.beamWidth).rotate(90);
                        float x3 = after.x - Tmp.v1.x, y3 = after.y - Tmp.v1.y, x4 = after.x + Tmp.v1.x, y4 = after.y + Tmp.v1.y;
                        Fill.quad(x1, y1, start.toFloatBits(), x2, y2, start.toFloatBits(), x3, y3, end.toFloatBits(), x4, y4, end.toFloatBits());

                        Draw.color(start);
                        if(before.collision) Fill.circle(before.x, before.y, source.beamWidth);
                        //if(debug) Draw.blend(); Draw.color(); Lines.line(before.x, before.y, after.x, after.y);
                    }
                    CollisionData data = beams.get(beams.size - 1);
                    if(data.collision) {
                        Draw.color(toColor(data.before));
                        Fill.circle(data.x, data.y, source.beamWidth);
                    }
                    //if(debug) Draw.color();Fill.circle(data.x, data.y, 1);
                });
                out.clear();
                tmps.clear();
                Draw.color();
                if(!debug) return;
                l.hitboxes(tmps);
                for (int i = 0; i < tmps.size; i++) {
                    WorldShape shape = tmps.get(i);
                    int size = shape.edges().length;
                    for (int j = 0; j < shape.edges().length; j += 2) {
                        float x3 = shape.getX() + shape.edges()[(j) % size];
                        float y3 = shape.getY() + shape.edges()[(j+1) % size];
                        float x4 = shape.getX() + shape.edges()[(j+2) % size];
                        float y4 = shape.getY() + shape.edges()[(j+3) % size];
                        Lines.line(x3, y3, x4, y4);
                    }
                }
            });
            Draw.reset();
            Draw.alpha(1);
            Draw.blend(Blending.additive);
            FrostShaders.effectBuffer.end();
            Draw.blit(FrostShaders.effectBuffer, FrostShaders.light);
            Draw.blend();
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
        tmpf[0] = color.r;
        tmpf[1] = color.g;
        tmpf[2] = color.b;
        tmpf[3] = falloff.r;
        tmpf[4] = falloff.g;
        tmpf[5] = falloff.b;

        float distance = 0;
        boolean found = false;
        //Find the max distance away from the start the beam can go, ignoring components already zero
        for (int i = 0; i < tmpf.length/2; i++) {
            float comp = tmpf[i];
            float dst = comp/tmpf[i + 3];
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
