package main.ai.types;

import arc.math.Mathf;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class ArtilleryAI extends GroundAI {
    @Override
    public void updateMovement(){

        Building core = unit.closestEnemyCore();

        if(core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)){
            target = core;
            for(var mount : unit.mounts){
                if(mount.weapon.controllable && mount.weapon.bullet.collidesGround){
                    mount.target = core;
                }
            }
        }

        if((core == null || !unit.within(core, unit.type.range * 0.5f))){
            boolean move = true;

            if(state.rules.waves && unit.team == state.rules.defaultTeam){
                Tile spawner = getClosestSpawner();
                if(spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
                if(spawner == null && core == null) move = false;
            }

            //no reason to move if there's nothing there
            if(core == null && (!state.rules.waves || getClosestSpawner() == null)){
                move = false;
            }

            //no reason to move if in range

            if(target != null && unit.within(target, unit.type.range)){
                if(target instanceof Unit) move = true;
                else if(target instanceof Building b){
                    for (BlockFlag flag: unit.type.targetFlags) {
                        if(b.block.flags.contains(flag)){
                            move = false;
                            break;
                        }
                    }
                }
            }

            if(move) pathfind(Pathfinder.fieldCore);
        }

        if(unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()){
            unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.riseSpeed);
        }

        faceTarget();
    }

    public Teamc target(float x, float y, float range, boolean air, boolean ground){
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground);
    }
}
