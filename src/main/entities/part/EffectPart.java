package main.entities.part;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.part.DrawPart;

public class EffectPart extends DrawPart {

    public DrawPart.PartProgress progress;
    public Seq<PartMove> moves = new Seq<>();

    public float effectChance = 0;
    public Effect effect = Fx.none;

    public float x, y, rotation, rangeRotation, rangeX, rangeY;
    public Color color = Color.white;
    public boolean mirror;

    @Override
    public void draw(PartParams partParams) {
        if(Vars.state.isPaused()) return;
        if(!Mathf.chance(Time.delta * effectChance * progress.get(partParams))) return;
        float px = x, py = y, prot = rotation, pRanX = rangeX, pRanY = rangeY;

        for (PartMove move: moves) {
            float prog = move.progress.get(partParams);
            px += move.x * prog;
            py += move.y * prog;
            pRanX += move.gx;
            pRanY += move.gy;
            prot += move.rot * prog;
        }

        Tmp.v1.set(px, py).add(Tmp.v2.set(Mathf.random(-pRanX, pRanX), Mathf.random(-pRanY, pRanY)).rotate(rangeRotation));
        Tmp.v2.set(Tmp.v1).rotate(partParams.rotation - 90);
        effect.at(partParams.x + Tmp.v2.x, partParams.y + Tmp.v2.y, prot + partParams.rotation);

        if(!mirror) return;

        Tmp.v3.set(Tmp.v1).scl(-1,1).rotate(partParams.rotation - 90);
        effect.at(partParams.x + Tmp.v3.x, partParams.y + Tmp.v3.y, partParams.rotation - rotation, color);
    }

    @Override
    public void load(String s) {

    }
}
