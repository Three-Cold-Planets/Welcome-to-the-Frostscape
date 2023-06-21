package main.ui.frag;

import main.ui.FrostUI;
import main.ui.frag.BlockSelectFrag.*;
import main.ui.frag.buttons.UpgradeSelectButton;
import main.world.blocks.light.SolarReflector;
import mindustry.gen.Icon;

import static main.ui.frag.BlockSelectFrag.buttons;

public class BlockSelectButtons {


    public static void setup(){

        //Add exit button
        buttons.add(new SelectButton("Exit", Icon.left, false, (b) -> true, (t, builds) -> FrostUI.select.hideConfig()));
        //Enable/Disable
        buttons.add(new SelectButton("Disable", Icon.cancel, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = false)));
        buttons.add(new SelectButton("Enable", Icon.play, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = true)));
        buttons.add(new SelectButton("Rotate", Icon.rotate, false, (b) -> {
            for (int i = 0; i < b.size; i++) {
                if(b.get(i) instanceof SolarReflector.SolarReflectorBuild){
                    return true;
                };
            }
            return false;
        }, (t, builds) -> {
            FrostUI.select.hideConfig();
        }));
        //Upgrades button
        buttons.add(new UpgradeSelectButton());
    }

}
