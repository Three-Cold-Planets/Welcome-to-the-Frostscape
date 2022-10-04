package frostscape.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FrostItems {
    //Ferric tech for all biomes
    public static Item magnetite, ferricPanels,
    //Culloi tech for snow
    culloi;
    public static void load(){
        magnetite = new Item("magnetite-shards"){{
            color = Color.valueOf("e7bdbd");
        }};

        ferricPanels = new Item("ferric-panels"){{
            color = Color.valueOf("#eee5f6");
        }};
    }
}
