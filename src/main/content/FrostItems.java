package main.content;

import arc.graphics.Color;
import main.graphics.ModPal;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.type.Item;

public class FrostItems {
    public static Item
    //Stones
    stone, limestone, cryolite,
    //Ferric/Aluminium tech for all biomes
    rust, magnetite, bauxite, ferricPanels, aluminium, sodium, calcium, drainCleaner, thermite, infernum,
    //Salts and clays
    hailite, lime,
    //Meltables items
    ice, snow,
    //Volcanic tech
    sulfur,
    //Gel tech
    algaeMuffin, gel, enzymes, boron;
    public static void load(){

        stone = new Item("stone"){{
            color = ModPal.stone;
        }};

        limestone = new Item("limestone"){{
            color = ModPal.limestone;
        }};

        cryolite = new Item("cryolite"){{
            color = ModPal.cryolite;
        }};

        rust = new Item("rust"){{
            color = ModPal.rust;
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

        snow = new Item("snow"){{
            color = Blocks.snow.mapColor;
        }};

        hailite = new Item("hailite"){{
            color = ModPal.salt;
        }};

        lime = new Item("lime"){{
            color = ModPal.lime;
        }};

        drainCleaner = new Item("soda-lye"){{
            color = ModPal.soda;
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

        calcium = new Item("calcium"){{
            color = ModPal.calcium;
        }};

        boron = new Item("boron"){{
            color = ModPal.boron;
        }};

        infernum = new Item("infernum"){{
            color = ModPal.infernum;
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
