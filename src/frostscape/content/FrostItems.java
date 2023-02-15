package frostscape.content;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import mindustry.content.Items;
import mindustry.type.Item;

public class FrostItems {
    //Ferric/Aluminium tech for all biomes
    public static Item magnetite, ferricPanels, aluminium, thermite,
    //Meltables items
    ice,
    //Volcanic tech
    sulphur,
    //Culloi tech for snow
    culloi;
    public static void load(){
        magnetite = new Item("magnetite-shards"){{
            color = Color.valueOf("e7bdbd");
        }};

        sulphur = new Item("sulphur"){{
            color = Palf.sulphur;
        }};

        aluminium = new Item("aluminium"){{
            color = Palf.aluminium;
        }};

        ice = new Item("ice"){{
            color = Palf.ice;
        }};

        ferricPanels = new Item("ferric-panels"){{
            color = Color.valueOf("#eee5f6");
        }};

        thermite = new Item("thermite"){{
            color = Items.pyratite.color.cpy().shiftSaturation(0.1f);
        }};
    }
}
