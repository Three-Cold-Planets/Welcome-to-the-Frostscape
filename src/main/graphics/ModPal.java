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

    ferricPanel = Color.valueOf("#eee5f6"),
    sodium = Color.valueOf("#96a1a6"),

    boron = Color.valueOf("a89edd"),

    ice = Color.valueOf("#9ccaed"),

    algae = Color.valueOf("#45283c"),

    enzimes = Color.valueOf("#6b2da8"),

    gel = Color.valueOf("#719537"),

    sulphur = Color.valueOf("#beab77"),

    salt = Color.valueOf("e8e4e3"),

    quiteDarkOutline = Color.valueOf("#39343c"),
    specialist = Color.valueOf("#c49ff4"),
    hunter = Color.valueOf("#dde6f0"),
    swarm = Color.valueOf("f0725c"),
    gelid = Color.valueOf("f4e888"),

    heat = new Color(1.0F, 0.22F, 0.22F, 0.8F),

    lightRed = Color.valueOf("#ee96c5"),
    darkRed = Color.valueOf("#9c4538"),
    lightGreen = Color.valueOf("#9ffba9"),
    darkGreen = Color.valueOf("#238b3f"),
    lightBlue = Color.valueOf("#a7ebf2"),
    darkBlue = Color.valueOf("#4066a9"),

    glowCyan = Color.valueOf("1baef8"),
    glowCyanTame = Color.valueOf("#d0f0eb"),
    glowYellow = Color.valueOf("f8b50d"),

    glowMagenta = Color.valueOf("bf08f7"),

    select = Color.valueOf("#a9cc6b");
    ;
}
