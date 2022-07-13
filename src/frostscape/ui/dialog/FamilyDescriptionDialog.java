package frostscape.ui.dialog;

import arc.Core;
import arc.scene.ui.*;
import frostscape.ui.Texf;
import frostscape.world.meta.Family;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class FamilyDescriptionDialog extends BaseDialog {

    public FamilyDescriptionDialog(String name){
        super(name);
        addCloseButton();
    }

    public void build(Family family){
        cont.clear();
        float maxWidth = Core.scene.getWidth();

        cont.table(table -> {
            table.table(t -> {
                t.image(family.flag).size(240, 120).left();
                t.table(info -> {
                    info.add("Family title: " + family.localizedName).left().padTop(60);
                    info.row();
                    info.add("[gray]Internal name: " + family.name).left().padTop(5);
                }).fillY();
            }).top().fillX();
            table.row();
            table.add(new Image()).width(maxWidth).height(10).color(Pal.darkishGray);
            table.row();
            table.table(t -> {
                t.pane(Styles.horizontalPane, left -> {
                    left.background(Tex.whiteui);
                    left.setColor(Pal.darkestGray);
                    left.add("[accent]<Description>").top().left().padLeft(15);
                    left.row();
                    left.add(family.description).left().padLeft(25).width(400f).wrap().fillX();
                    left.row();
                    left.add("[accent]<Details>").top().left().padLeft(15).padTop(40);
                    left.row();
                    left.add(family.details).left().padLeft(25).width(400f).wrap().fillX();
                    left.row();
                    left.table(end -> {
                        end.add("END OF DOCUMENT");
                        end.background(Texf.diagonalBoundary);
                    }).top().width(maxWidth/2);
                }).scrollX(false).width(maxWidth/2).left();

                t.pane(right -> {
                    right.background(Tex.whiteui);
                    right.setColor(Pal.darkestGray);
                    right.table(end -> {
                        end.add("END OF DOCUMENT");
                        end.background(Texf.diagonalBoundary);
                    }).top().width(maxWidth/2);
                }).width(maxWidth/2).height(600).right();
            }).height(600).padTop(10);

        }).fill();

        show();
    }
}
