package frostscape.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FrostItems {
    public static Item magnetite;
    public static void load(){
        magnetite = new Item("magnetite-shards"){{
            color = Color.valueOf("e7bdbd");
        }};
    }
}
