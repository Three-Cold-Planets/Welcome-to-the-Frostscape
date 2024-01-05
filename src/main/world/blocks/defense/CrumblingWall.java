package main.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import main.world.UpgradesType;
import main.world.blocks.drawers.UpgradeDrawer;
import mindustry.entities.TargetPriority;
import mindustry.gen.Bullet;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class CrumblingWall extends BaseBlock implements UpgradesType {

    //Flat increase to armor that goes up as the wall is damaged.
    public int plating;

    private static float prevArmor;

    public CrumblingWall(String name){
        super(name);
        solid = true;
        destructible = true;
        group = BlockGroup.walls;
        update = false;
        canOverdrive = false;
        drawDisabled = false;
        crushDamageMultiplier = 5f;
        priority = TargetPriority.wall;
        envEnabled = Env.any;
        plating = 5;
        variants = 4;
    }

    @Override
    public Seq<UpgradeDrawer> drawers() {
        return null;
    }

    public class CrumblingWallBuild extends BaseBuilding {
        public int stage = 1;

        public boolean collision(Bullet other) {
            prevArmor = armor;
            armor += plating * (1 - healthf());
            boolean collided = super.collision(other);
            armor = prevArmor;
            return collided;
        }

        @Override
        public void damage(float damage) {
            super.damage(damage);
            updateState();
        }

        public void updateState(){
            stage = Mathf.clamp(Mathf.ceil((1 - healthf()) * variants), 1, variants);
        }

        @Override
        public void draw() {
            Draw.rect(variantRegions[stage - 1], this.x, this.y, this.drawrot());
            this.drawTeamTop();
        }

        @Override
        public void readBase(Reads read) {
            super.readBase(read);
            updateState();
            Log.info(health);
        }
    }
}
