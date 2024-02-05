package main.content;

import arc.graphics.Color;
import main.graphics.ModPal;
import mindustry.content.Items;
import mindustry.type.Item;

public class FrostItems {
    //Ferric/Aluminium tech for all biomes
    public static Item stone, rust, magnetite, bauxite, ferricPanels, aluminium, sodium, thermite,
    //Salts and clays
    hailite,
    //Meltables items
    ice,
    //Volcanic tech
    sulfur,
    //Gel tech
    algaeMuffin, gel, enzymes, boron;
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

        ice = new Item("ice"){{
            color = ModPal.ice;
        }};

        hailite = new Item("hailite"){{
            color = ModPal.salt;
        }};

        sulfur = new Item("sulfur"){{
            color = ModPal.sulphur;
            flammability = 0.6f;
            explosiveness = 0.25f;
        }};

        ferricPanels = new Item("ferric-panels"){{
            color = ModPal.ferricPanel;
        }};

        aluminium = new Item("aluminium"){{
            color = ModPal.aluminium;
        }};

        sodium = new Item("sodium"){{
            color = ModPal.sodium;
        }};

        boron = new Item("boron"){{
            color = ModPal.boron;
        }};

        thermite = new Item("thermite"){{
            color = Items.pyratite.color.cpy().shiftSaturation(0.1f);
            flammability = 2;
        }};

        enzymes = new Item("enzymes"){{
            color = ModPal.enzimes;
        }};

        algaeMuffin = new Item("algae-muffin"){{
            color = ModPal.algae;
        }};

        gel = new Item("gel"){{
            color = ModPal.gel;
        }};
    }
}
