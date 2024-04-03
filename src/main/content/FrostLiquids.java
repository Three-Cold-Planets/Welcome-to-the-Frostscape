package main.content;

import arc.graphics.Color;
import main.graphics.ModPal;
import main.type.liquid.ColourChangeLiquid;
import mindustry.type.Liquid;

public class FrostLiquids {
    public static Liquid vitriol, carbonDioxide, steam, hydrogenFlouride, chlorine, acetylene;

    public static void load(){
        vitriol = new ColourChangeLiquid(("vitriol")){{
            colors = new Color[]{
                    Color.valueOf("8b653c"),
                    Color.valueOf("b99437"),
                    Color.valueOf("c7bb6b"),
                    Color.valueOf("ece6da")
            };
            frames = 8;
            frameTime = 15;
            transitionTime = 60;
        }};

        carbonDioxide = new Liquid("carbon-dioxide"){{
            gas = true;
            gasColor = color = ModPal.carbonDioxide;
        }};

        steam = new Liquid("steam"){{
            gas = true;
            gasColor = color = ModPal.steam;
        }};

        hydrogenFlouride = new Liquid("hydrogen-flouride"){{
            gas = true;
            gasColor = color = ModPal.hydrogenFlouride;
        }};

        chlorine = new Liquid("chlorine"){{
            gas = true;
            gasColor = color = ModPal.chlorine;
        }};

        acetylene = new Liquid("acetylene"){{
            gas = true;
            gasColor = color = ModPal.acetylene;
        }};
    }
}
