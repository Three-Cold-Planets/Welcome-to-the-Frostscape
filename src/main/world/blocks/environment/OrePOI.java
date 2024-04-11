package main.world.blocks.environment;

import main.world.blocks.MarkerBlock;
import mindustry.entities.TargetPriority;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

public class OrePOI extends Block {

    public OrePOI(String name) {
        super(name);
        solid = true;
        destructible = true;

        group = BlockGroup.walls;
        buildCostMultiplier = 6f;
        canOverdrive = false;
        drawDisabled = false;
        crushDamageMultiplier = 5f;
        priority = TargetPriority.wall;

        //it's a wall of course it's supported everywhere
        envEnabled = Env.any;

        buildVisibility = BuildVisibility.sandboxOnly;
    }

    public class OrePOIBuild extends Building implements MarkerBlock{

        public boolean found;

        @Override
        public boolean found() {
            return found;
        }

        @Override
        public boolean setFound() {
            return found;
        }
    }
}
