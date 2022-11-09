package frostscape.util;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.struct.Seq;
import arc.util.*;
import frostscape.ui.FrostUI;
import frostscape.world.meta.Family;
import frostscape.world.meta.stat.FrostStats;
import mindustry.content.UnitTypes;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.Stats;

public class StatUtils {
    public static float cycleSpeed = 2f;

    public static void addFamilyStats(Stats stats, Seq<Family> families){
        stats.add(FrostStats.familyLink, table -> {
            table.row();
            families.each(family -> {
                table.table(t -> {
                    t.setBackground(Tex.whiteui);
                    Color color = Pal.darkestGray.cpy();

                    ClickListener listener = new ClickListener();
                    t.addListener(listener);
                    t.addListener(new HandCursorListener());

                    Label label = new Label("More info");

                    t.table(tlabel -> {
                        tlabel.image(family.icon);
                        tlabel.add(label).size(160, 80);
                    }).left();

                    t.update(() -> {
                        t.setColor(color.lerp(listener.isOver() ? Pal.lightishGray : Pal.darkestGray, Time.delta));
                        label.setColor(Tmp.c1.set(Color.white).lerp(family.color, Mathf.sin(Time.time * cycleSpeed)));
                    });

                    t.clicked(() -> {
                        FrostUI.family.build(family);
                    });
                }).size(320, 80);
                table.row();
            });
        });
    }

    public static void addScanningStats(Block b){
        if(b.minfo.mod != null) {
            b.stats.add(FrostStats.envCategory, Core.bundle.get(b.getContentType().name() + "." + b.name + ".env-category", b.minfo.mod.meta.displayName));
            return;
        }
        b.stats.add(FrostStats.envCategory, Core.bundle.get(b.getContentType().name() + "." + b.name + ".env-category", "category.scanning.vanilla"));
    }
}
