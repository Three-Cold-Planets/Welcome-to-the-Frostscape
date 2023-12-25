package main.content;

import arc.graphics.Color;
import main.graphics.ModPal;
import mindustry.content.Items;
import mindustry.type.Item;

public class FrostItems {
    //Ferric/Aluminium tech for all biomes
    public static Item stone, rust, magnetite, bauxite, ferricPanels, aluminium, thermite,
    //Salts and clays
    hailite,
    //Meltables items
    ice,
    //Volcanic tech
    sulphur,
    //Culloi tech for snow
    culloi;
    public static void load(){

        stone = new Item("stone"){{
            color = Color.valueOf("8f8f94");
        }};

        rust = new Item("rust"){{
            color = Color.valueOf("d0997a");
        }};

        magnetite = new Item("magnetite-shards"){{
            color = Color.valueOf("e7bdbd");
        }};

        bauxite = new Item("bauxite"){{
            color = Color.valueOf("c77663");
        }};

        sulphur = new Item("sulphur"){{
            color = ModPal.sulphur;
        }};

        aluminium = new Item("aluminium"){{
            color = ModPal.aluminium;
        }};

        ferricPanels = new Item("ferric-panels"){{
            color = Color.valueOf("#eee5f6");
        }};

        thermite = new Item("thermite"){{
            color = Items.pyratite.color.cpy().shiftSaturation(0.1f);
        }};

        hailite = new Item("hailite"){{
            color = ModPal.salt;
        }};

        ice = new Item("ice"){{
            color = ModPal.ice;
        }};
    }
}
