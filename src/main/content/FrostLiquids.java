package main.content;

import arc.graphics.Color;
import main.type.liquid.ColourChangeLiquid;
import mindustry.type.Liquid;

public class FrostLiquids {
    public static Liquid vitriol;

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
    }
}
