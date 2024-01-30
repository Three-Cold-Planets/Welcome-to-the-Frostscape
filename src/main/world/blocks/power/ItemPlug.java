package main.world.blocks.power;

import main.world.blocks.PlugBlock;
import main.world.systems.bank.ResourceBankHandler;
import mindustry.gen.Building;
import mindustry.type.Item;

// Tries to keep its inventory at the target stock. Imports/exports accordingly.
public class ItemPlug extends PlugBlock {

    //Max items it can pull at once
    public float maxExchanged = 4;

    //Time in ticks till importing/exporting starts.
    public float importDelay = 140;

    public ItemPlug(String name) {
        super(name);
    }

    public class ItemPlugBuild extends PlugBuild{

        //Percent of item capacity to keep full.
        public float target = 0.5f;

        //Percent margin on either side of the target.
        public float margin = 0.1f;

        public float exchangeTime = 0;

        public int id;

        public Item stockItem;

        @Override
        public void exchange() {
            super.exchange();
        }

        public boolean acceptItem(Building source, Item item) {
            return items.total() < itemCapacity && ResourceBankHandler.itemCap > 0;
        }
    }

    private enum BusState{
        importing("bar.bank.importing"),
        exporting("bar.bank.exporting"),
        stable("bar.bank.stable"),

        full("bar.bank.full"),
        empty("bar.bank.empty"),
        disabled("bar.bank.disabled");

        final String name;

        BusState(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
