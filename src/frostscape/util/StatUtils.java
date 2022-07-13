package frostscape.util;

import arc.graphics.Color;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.util.Log;
import arc.util.Time;
import frostscape.ui.FrostUI;
import frostscape.world.meta.Family;
import frostscape.world.meta.stat.FrostStats;
import mindustry.content.UnitTypes;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Stats;

public class StatUtils {
    public static void addFamilyStats(Stats stats, Family family){
        stats.add(FrostStats.familyName, family.localizedName);
        stats.add(FrostStats.familyLink, table -> {
            table.row();
            table.table(t -> {
                t.setBackground(Tex.whiteui);
                Color color = Pal.darkestGray.cpy();

                ClickListener listener = new ClickListener();
                t.addListener(listener);
                t.addListener(new HandCursorListener());

                t.table(label -> {
                    label.image(family.icon);
                    label.add("More info").size(160, 80);
                }).left();

                t.update(() -> {
                    t.setColor(color.lerp(listener.isOver() ? Pal.lightishGray : Pal.darkestGray, Time.delta));
                });

                t.clicked(() -> {
                    FrostUI.family.build(family);
                });
            }).size(320, 80);
        });
    }
}
