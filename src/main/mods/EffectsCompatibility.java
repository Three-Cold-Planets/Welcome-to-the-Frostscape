package main.mods;

import arc.math.geom.Vec2;
import arc.util.Log;
import main.entities.bullet.BouncyBulletType;
import main.entities.effect.DataEffect;
import main.entities.effect.FrostEffect;
import main.math.Math3D;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.gen.EffectState;
import mindustry.world.blocks.defense.turrets.PointDefenseTurret;

public class EffectsCompatibility {
    public static Vec2 tv1;

    public static void handle(){
        Vars.content.blocks().each(b -> {
            if(b instanceof PointDefenseTurret turret){
                turret.beamEffect = parallaxEffect(turret.beamEffect);
            }
        });
    }

    public static DataEffect parallaxEffect(Effect effect){
        return new DataEffect(){{
            child = FrostEffect.convert(effect);
            renderer = e -> {
                Log.info("WHAT THE FUCK-");
                if(!(e.data instanceof BeamData)){
                    Log.info("Not beam data");
                    EffectState state = (EffectState) e.data;
                    if(state.data == null) return;
                    Log.info(state.data);
                    Log.info(state.data instanceof Bullet);
                    Log.info(((Bullet) state.data).data instanceof Math3D.HeightHolder);
                    if(!(state.data != null && state.data instanceof Bullet bullet && bullet.data instanceof Math3D.HeightHolder)) return;
                    e.data = new BeamData(state);
                    Log.info("Converted data to new beam data");
                }
                BeamData data = (BeamData) e.data;

                BouncyBulletType.getBulletPos(data.bullet.x, data.bullet.y, BouncyBulletType.getHeight(data.bullet), tv1);
                data.state.data = tv1;
            };
        }};
    }

    public static class BeamData{
        public BeamData(EffectState state){
            this.state = state;
            Bullet bullet = (Bullet) state.data;
            this.bullet = bullet;
            pos.set(bullet.x, bullet.y);
        }
        public EffectState state;
        public Bullet bullet;
        public Vec2 pos = new Vec2();
    }
}
