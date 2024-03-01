package main.entities.comp;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import ent.anno.Annotations;
import mindustry.gen.Unitc;
import mindustry.graphics.Layer;

@Annotations.EntityComponent
abstract class PortaLaserComp implements Unitc {
    @Annotations.Import float x, y, rotation;

    float duoRotation = 0;

    @Override
    public void update() {
        duoRotation = Angles.moveToward(duoRotation, rotation, Time.delta);
    }

    @Override
    public void draw(){
        Draw.z(Layer.effect);
        Draw.rect(Core.atlas.find("duo"), x, y + Mathf.sin(45, 20), duoRotation - 90 + Mathf.sin(10, 45));
    }
}
