package main.entities.ability;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

//Damages the unit while it shoots. Useful for units with "timed" fuses
public class SelfDamageAbility extends Ability {
    public Effect effect;
    public float effectChance;

    public float damage;
    public boolean pierceArmor;

    public Rect effectField;
    public Color color;

    @Override
    public void update(Unit unit) {
        super.update(unit);

        if(!unit.isShooting()) return;

        if(pierceArmor) unit.damageContinuousPierce(damage);
        else unit.damageContinuous(damage);

        if(Mathf.randomBoolean(effectChance * Time.delta)){
            Tmp.v1.set(effectField.x + Mathf.random(effectField.width), effectField.y + Mathf.random(effectField.height)).rotate(unit.rotation - 90).add(unit);
            effect.at(Tmp.v1.x, Tmp.v1.y, unit.rotation, color);
        }
    }
}
