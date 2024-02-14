package main.world.blocks;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;

public class BankBlock extends Block {
    public BankBlock(String name) {
        super(name);
        update = false;
        destructible = false;
        consumesPower = true;
        hasPower = true;
        hasItems = true;
        hasLiquids = true;
        outputsPower = true;
        inEditor = false;
        consumePowerBuffered(0);
    }

    public class BankBuild extends Building {
        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < itemCapacity;
        }
    }
}
