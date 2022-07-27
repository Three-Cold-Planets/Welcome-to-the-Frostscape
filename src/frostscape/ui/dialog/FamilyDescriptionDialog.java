package frostscape.ui.dialog;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import frostscape.content.Families;
import frostscape.ui.Texf;
import frostscape.world.meta.Family;
import mindustry.Vars;
import mindustry.ctype.*;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Weather;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.DatabaseDialog;

import java.util.Locale;

import static mindustry.Vars.mobile;

public class FamilyDescriptionDialog extends BaseDialog {

    private String endingMessage = "END OF DOCUMENT";
    private int buttons = 0;

    public float maxWidth = 0;
    public Family current;
    public Table unitList = new Table();
    public TextField search;
    public FamilyDescriptionDialog(String name){
        super(name);
        addCloseButton();
    }

    public void build(Family family){
        cont.clear();
        maxWidth = Core.scene.getWidth();
        current = family;

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

                t.pane(unitList).width(maxWidth/2).height(600).right();

            }).height(600).padTop(10);
            table.row();
            table.table(s -> {
                s.image(Icon.zoom).left();
                s.table(sbar -> {
                    search = sbar.field(null, text -> rebuildList()).width(maxWidth/2 - Icon.zoom.getLeftWidth()).get();
                    search.setMessageText(Core.bundle.get("players.search"));
                    search.addInputDialog();
                }).left().padLeft(10);
            }).padLeft(maxWidth/2);

        }).fill();

        show();
    }

    public void rebuildList(){
        unitList.clear();
        unitList.background(Tex.whiteui);
        unitList.setColor(Pal.darkestGray);
        unitList.table(table -> {
            ObjectMap<ContentType, Seq<UnlockableContent>> map = new ObjectMap<>();

            current.members.each(content -> {
                if (!map.containsKey(content.getContentType())) {
                    map.put(content.getContentType(), Seq.with(content));
                    return;
                }
                map.get(content.getContentType()).add(content);
            });

            buttons = 0;

            endingMessage = "END OF DOCUMENT";

            int columns = Mathf.floor(maxWidth/60);

            map.each((type, members) -> {
                members.each(member -> {
                        if (!member.localizedName.contains(search.getText()) && !(search.getText() == "")) return;

                        if((buttons++ % columns) == 0) table.row();
                        Image image = new Image(member.uiIcon);
                        ClickListener listener = new ClickListener();
                        image.addListener(listener);
                        if(!mobile){
                            image.addListener(new HandCursorListener());
                            image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                        }
                        image.clicked(() -> {
                            hide();
                            Vars.ui.content.show(member);
                        });
                        table.add(image).size(60);
                });
            });

            if(buttons == 0) endingMessage = "NO RESULTS FOUND";
        });

        unitList.row();
        unitList.table(end -> {
            end.add(endingMessage);
            end.background(Texf.diagonalBoundary);
        }).top().width(maxWidth / 2);
    }
}
