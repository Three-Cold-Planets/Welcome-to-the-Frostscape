package main.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

//Friend :D
public class ModPal {
    public static Color
    //:(
    pulseChargeStart = new Color(Color.sky).lerp(Pal.lightTrail, 0.35f),
    pulseChargeEnd = new Color(Color.sky).lerp(Pal.lightTrail, 0.25f).lerp(Color.valueOf("#a4ddf2"), 0.05f),

    aluminium = Color.valueOf("#a0a3b4"),

    ice = Color.valueOf("#9ccaed"),
    sulphur = Color.valueOf("#beab77"),

    salt = Color.valueOf("e8e4e3"),

    quiteDarkOutline = Color.valueOf("#39343c"),
    hunter = Color.valueOf("#dde6f0"),
    specialist = Color.valueOf("#c49ff4"),

    heat = new Color(1.0F, 0.22F, 0.22F, 0.8F),

    lightRed = Color.valueOf("#ee96c5"),
    darkRed = Color.valueOf("#9c4538"),
    lightGreen = Color.valueOf("#9ffba9"),
    darkGreen = Color.valueOf("#238b3f"),
    lightBlue = Color.valueOf("#a7ebf2"),
    darkBlue = Color.valueOf("#4066a9"),

    select = Color.valueOf("#a9cc6b");
    ;
}
