package main.ui.dialog.database;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import main.world.meta.Family;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

public class FamiliesTable extends Table {
    private TextField search;
    private boolean showLocked = true;
    private Table all = new Table(), cont = new Table();
    public FamiliesTable(){
        add(cont);
        all.margin(20).marginTop(0);
        cont.table((s) -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, (text) -> {
                rebuild();
            }).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();
        cont.pane(all).scrollX(false);
        Events.run(EventType.UnlockEvent.class, this::rebuild);
    }

    public void rebuild(){
        all.clear();
        String text = search.getText();
        int cols = (int) Mathf.clamp(((float) Core.graphics.getWidth() - Scl.scl(30.0F)) / Scl.scl(340.0F), 1.0F, 22.0F);

        int i = 0;
        for(Family family: Family.all){
            all.table(table -> {
                table.background(Tex.button);
                table.add(new Image(family.flag)).size(240, 120);
                table.row();
                table.add(family.localizedName).center();
            }).size(300, 160);
            i++;
            if(i > cols){
                i = 0;
                all.row();
            }
        };
    }
}
