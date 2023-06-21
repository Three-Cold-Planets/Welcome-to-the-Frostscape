package main.world.blocks.defense;

import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import static mindustry.Vars.tilesize;

public class MinRangeTurret extends ItemTurret {

    public float minRange = -1;

    public MinRangeTurret(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
        if(minRange == -1) minRange = range/2;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
    }

    public class MinRangeTurretBuild extends ItemTurretBuild {
        @Override
        public void targetPosition(Posc pos) {
            super.targetPosition(pos);
            Tmp.v1.set(targetPos).sub(x, y);
            if(Tmp.v1.len() < minRange) Tmp.v1.trns(Tmp.v1.angle(), minRange);
            targetPos.set(Tmp.v1.add(x, y));
        }

        @Override
        protected void findTarget() {
            float range = range();

            if (targetAir && !targetGround) {
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e) && dst(e) > minRange, unitSort);
            } else {
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround) && dst(e) > minRange, b -> targetGround && buildingFilter.get(b) && dst(b) > minRange, unitSort);

                if (target == null && canHeal()) {
                    target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this && dst(b) > minRange);
                }
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, minRange, team.color);
        }
    }
}
