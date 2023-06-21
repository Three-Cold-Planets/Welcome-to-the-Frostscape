package main.ui.dialog.settings;

import arc.func.Prov;
import arc.scene.ui.layout.Table;
import main.Frostscape;
import main.game.SectorController;
import main.ui.FrostUI;
import mindustry.game.Rules;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

import static main.game.ScriptedSectorHandler.controllers;
import static main.game.ScriptedSectorHandler.saveKey;

public class ControllerSettingDialog extends BaseDialog {
    Rules rules;
    private Table main, selectP;
    private Prov<Rules> resetter;
    private SectorController controller;

    BaseDialog select;

    public ControllerSettingDialog(String title) {
        super(title);
        addCloseButton();

        setFillParent(true);
        shown(this::setup);

        select = new BaseDialog("@select");
        select.addCloseButton();
    }

    public void show(Rules rules, Prov<Rules> resetter) {
        this.rules = rules;
        this.resetter = resetter;
        show();
    }

    public void setup(){
        cont.clear();
        cont.pane(m -> main = m).scrollX(false);
        main.margin(10f);

        main.left().defaults().fillX().left().pad(5);
        main.row();

        title("@rules.title.controller");
        main.row();

        main.label(() -> controller == null ? "[gray]<none>" : controller.localizedName);
        main.row();
        main.button("@controller",
                () -> {
                    rebuildSelect();
                    select.show();
                }
        ).left().width(300f).row();
        Frostscape.sectors.reRead();
        controller = Frostscape.sectors.controller;
    }

    void rebuildSelect(){
        select.cont.clear();
        select.cont.pane(p -> selectP = p).scrollX(false);
        selectP.button("[gray]<none>", () -> {
            if(rules.tags.containsKey(saveKey)) rules.tags.remove(saveKey);
            controller = null;
            select.hide();
            FrostUI.SCHSettings.hide();
        }).width(180);
        selectP.row();
        controllers.each(c -> {
            selectP.button(c.localizedName, () -> {
                controller = c;
                rules.tags.put(saveKey, c.name);
                select.hide();
            }).width(180);
            selectP.row();
        });
    }

    void title(String text){
        main.add(text).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3);
        main.row();
        main.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20);
        main.row();
    }
}
