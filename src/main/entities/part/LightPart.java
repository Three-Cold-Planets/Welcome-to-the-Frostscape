package main.entities.part;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class LightPart extends DrawPart {

    public float radius = 15f;
    public float length = 0;

    /** Progress function for determining position/rotation. */
    public PartProgress progress = PartProgress.warmup;
    /** Progress function for scaling. */
    public PartProgress growProgress = PartProgress.warmup;
    public Seq<PartMove> moves = new Seq<>();

    public float x, y, xScl = 1f, yScl = 1f, rotation;
    public float moveX, moveY, growX, growY, moveRot;
    public Color color = Pal.powerLight;
    public float ocapacity = 1, stroke = 5;
    public boolean mirror = false;

    @Override
    public void draw(PartParams params) {

        float prog = progress.getClamp(params), sclProg = growProgress.getClamp(params);
        float mx = moveX * prog, my = moveY * prog, mr = moveRot * prog + rotation,
                gx = growX * sclProg, gy = growY * sclProg;

        if(moves.size > 0){
            for(int i = 0; i < moves.size; i++){
                var move = moves.get(i);
                float p = move.progress.getClamp(params);
                mx += move.x * p;
                my += move.y * p;
                mr += move.rot * p;
                gx += move.gx * p;
                gy += move.gy * p;
            }
        }

        float preXscl = Draw.xscl, preYscl = Draw.yscl;
        Draw.xscl *= xScl + gx;
        Draw.yscl *= yScl + gy;


        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        for(int s = 0; s < len; s++){
            //use specific side if necessary
            int i = params.sideOverride == -1 ? s : params.sideOverride;

            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

            float
                    rx = params.x + Tmp.v1.x,
                    ry = params.y + Tmp.v1.y,
                    rot = mr * sign + params.rotation;

            Draw.xscl *= sign;

            Drawf.light(rx,ry,radius * sclProg, color, ocapacity * prog);
            if(length > 0){
                Tmp.v2.trns(rot, length * sclProg).add(rx,ry);
                Drawf.light(rx,ry,Tmp.v2.x, Tmp.v2.y, stroke * sclProg, color, ocapacity * prog);
            }

            Draw.xscl *= sign;
        }
        Draw.scl(preXscl, preYscl);
    }

    @Override
    public void load(String name) {

    }
}
