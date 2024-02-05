package main.util;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Label;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import main.ui.FrostUI;
import main.world.meta.Family;
import main.world.meta.stat.FrostStats;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.Stats;

public class StatUtils {
    public static float cycleSpeed = 0.04f;
    public static float offset = 0;

    public static void addFamilyStats(Stats stats, Seq<Family> families){
        offset = 0;
        stats.add(FrostStats.familyLink, table -> {
            table.row();

            families.each(family -> {
                offset += Mathf.pi/4;
                table.table(t -> {
                    t.setBackground(Tex.whiteui);
                    Color color = Pal.darkestGray.cpy();

                    Label label = new Label("More info");

                    t.table(tlabel -> {
                        tlabel.image(family.icon);
                        tlabel.add(label).size(160, 80);
                    }).left();

                    ClickListener listener = new ClickListener();
                    t.addListener(listener);
                    t.addListener(new HandCursorListener());

                    t.update(() -> {
                        t.setColor(color.lerp(listener.isOver() ? Pal.lightishGray : Pal.darkestGray, Time.delta));
                        label.setColor(Tmp.c1.set(Color.white).lerp(family.color, Mathf.sin(Time.time * cycleSpeed + offset)));
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
        b.stats.add(FrostStats.envCategory, getCategory(b));
    }
    public static String getCategory(Block b){
        if(b.minfo.mod != null) {
            return Core.bundle.get(b.getContentType().name() + "." + b.name + ".env-category", b.minfo.mod.meta.displayName);
        }
        return Core.bundle.get(b.getContentType().name() + "." + b.name + ".env-category", Core.bundle.get("category.scanning.vanilla"));
    }
}
