package main.ui;

import arc.Core;
import arc.scene.style.Drawable;

public class ModTex {

    public static Drawable
    hunter, diagonalBoundary;


    public static void load(String modname){
        hunter = Core.atlas.drawable(modname + "-hunter");
        diagonalBoundary = Core.atlas.drawable(modname + "-diagonal-boundary");
    }
}
