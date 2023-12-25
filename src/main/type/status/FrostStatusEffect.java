package main.type.status;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.Tmp;
import main.world.meta.stat.FrostStats;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class FrostStatusEffect extends StatusEffect {
    public float shieldDamageMultiplier = 0;

    public FrostStatusEffect(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.addPercent(FrostStats.shieldDamageMultiplier, damageMultiplier);
    }

    @Override
    public void update(Unit unit, float time) {
        if(damage > 0){
            if(unit.shield > 0){
                //Don't bother applying extra damage to shield, just break it
                if(unit.shield < damage * shieldDamageMultiplier) {
                    unit.damagePierce(unit.shield + 1);
                    unit.damageContinuousPierce(damage);
                }
                else unit.damageContinuousPierce(damage * shieldDamageMultiplier);
            }
            else unit.damageContinuousPierce(damage);
        }else if(damage < 0){ //heal unit
            unit.heal(-1f * damage * Time.delta);
        }

        if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }

    public ObjectMap getTransitions(){
        return transitions;
    }
}
