package frostscape.world.blocks.core;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.content.Palf;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.UnitEntity;
import mindustry.graphics.*;
import mindustry.world.Tile;

public class BuildBeamCore extends FrostscapeCore{
    private static TextureRegion unitRegion;
    public float srotSpeed = 1.2f, mountRotSpeed = 2.2f;
    public Interp srotScaling = Interp.smooth;

    public Seq<Vec2> mountPoses = Seq.with();

    public TextureRegion mountRegion;

    public BuildBeamCore(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        mountRegion = Core.atlas.find(name + "-mount");
    }

    public class BuildBeamCoreBuild extends FrostscapeCoreBuild{
        public float[] mountRotations;
        public float srot = 0;
        public float srotVel = 0;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            mountRotations = new float[mountPoses.size];
            for (int i = 0; i < mountPoses.size; i++) {
                mountRotations[i] = Angles.angle(Tmp.v1.set(mountPoses.get(i)).x, Tmp.v1.y);
            }
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            srotVel = Mathf.lerpDelta(srotVel, building ? srotSpeed : 0, warmup);
            srot += srotScaling.apply(srotVel);
            
            if(building) for (int i = 0; i < mountRotations.length; i++) {
                mountRotations[i] = Angles.moveToward(mountRotations[i], Tmp.v1.set(mountPoses.get(i)).add(x, y).angleTo(constructPos), mountRotSpeed * Time.delta);
            }
            else for (int i = 0; i < mountRotations.length; i++) {
                mountRotations[i] += Time.delta *
                        ((0.75f + Mathf.sin(Time.time/10 + i * 90, 1, 3) * 0.25f)
                        + (0.75f + Mathf.sin(Time.time/15 + i * 90, 1, 3) * 0.25f));
            }
        }


        public void drawMounts(){
            float cx = constructPos.x, cy = constructPos.y;
            float invalidWarmup = 1 - warmup;

            for (int i = 0; i < mountPoses.size; i++) {
                Draw.rect(mountRegion, mountPoses.get(i).x + x, mountPoses.get(i).y + y, mountRotations[i] - 90);
            }

            if(entry == null) return;
            unitRegion = entry.type.fullIcon;

            Draw.draw(Layer.blockBuilding, () -> {
                Draw.color(Pal.accent, warmup);

                Shaders.blockbuild.region = entry.type.fullIcon;
                Shaders.blockbuild.time = Time.time;
                Shaders.blockbuild.progress = Mathf.clamp(progressf() + 0.05f);

                Draw.rect(entry.type.fullIcon, cx, cy);

                Draw.flush();
                Draw.color();
            });

            //draw unit silhouette
            Draw.mixcol(Tmp.c1.set(Pal.accent).lerp(Pal.remove, invalidWarmup), 1f);
            Draw.alpha(warmup);

            Draw.z(Layer.effect);
            Lines.square(cx, cy, entry.type.hitSize + 3.8f, srot);
            Lines.square(cx, cy, entry.type.hitSize + 3.8f, -srot);

            Draw.z(Layer.buildBeam);

            for (int i = 0; i < mountRotations.length; i++) {
                Tmp.v1.trns(mountRotations[i], mountRegion.height/8).add(mountPoses.get(i)).add(x, y);
                Drawf.buildBeam(Tmp.v1.x, Tmp.v1.y, cx, cy, entry.type.hitSize);
            }
            Fill.square(cx, cy, entry.type.hitSize);
        }

        @Override
        public void draw() {
            super.draw();
            drawMounts();
        }
    }
}
