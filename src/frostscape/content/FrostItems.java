package frostscape.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FrostItems {
    public static Item magnetite, ferricPanels;
    public static void load(){
        magnetite = new Item("magnetite-shards"){{
            color = Color.valueOf("e7bdbd");
        }};

        ferricPanels = new Item("ferric-panels"){{
            color = Color.valueOf("#eee5f6");
        }};
    }
}
