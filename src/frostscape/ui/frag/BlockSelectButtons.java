package frostscape.ui.frag;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.ui.FrostUI;
import frostscape.ui.frag.BlockSelectFrag.*;
import frostscape.ui.frag.buttons.UpgradeSelectButton;
import frostscape.world.blocks.light.SolarReflector;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import static frostscape.ui.frag.BlockSelectFrag.buttons;

public class BlockSelectButtons {


    public static void setup(){

        //Add exit button
        buttons.add(new SelectButton("Exit", Icon.left, false, (b) -> true, (t, builds) -> FrostUI.select.hideConfig()));
        //Enable/Disable
        buttons.add(new SelectButton("Disable", Icon.cancel, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = false)));
        buttons.add(new SelectButton("Enable", Icon.play, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = true)));
        buttons.add(new SelectButton("Enable", Icon.play, false, (b) -> {boolean found = false;
            for (int i = 0; i < b.size; i++) {
                if(b.get(i) instanceof SolarReflector.SolarReflectorBuild);
            }

        }, (t, builds) -> builds.each(b -> b.enabled = true)));
        //Upgrades tables
        buttons.add(new UpgradeSelectButton());
    }

}
