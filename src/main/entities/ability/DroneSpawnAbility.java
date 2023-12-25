package main.entities.ability;

import arc.Events;
import arc.math.Angles;
import arc.util.Time;
import main.entities.unit.DroneUnit;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.abilities.UnitSpawnAbility;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

import static mindustry.Vars.state;

public class DroneSpawnAbility extends UnitSpawnAbility {

    public int limit;

    public DroneSpawnAbility(UnitType unit, float spawnTime, float spawnX, float spawnY, int limit){{
        this.unit = unit;
        this.spawnTime = spawnTime;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.limit = limit;
    }}

    @Override
    public void update(Unit unit){
        timer += Time.delta * state.rules.unitBuildSpeed(unit.team);

        if(timer >= spawnTime && Units.canCreate(unit.team, this.unit)){
            float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
            spawnEffect.at(x, y, 0f, parentizeEffects ? unit : null);
            Unit u = this.unit.create(unit.team);
            u.set(x, y);
            u.rotation = unit.rotation;
            Events.fire(new EventType.UnitCreateEvent(u, null, unit));
            if(!Vars.net.client()){
                u.add();
            }

            if(u instanceof DroneUnit drone) drone.parent = unit;

            timer = 0f;
        }
    }
}
