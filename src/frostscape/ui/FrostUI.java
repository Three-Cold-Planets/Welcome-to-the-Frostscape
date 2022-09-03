package frostscape.ui;

import arc.scene.ui.Dialog;
import arc.util.Reflect;
import frostscape.ui.dialog.FamilyDescriptionDialog;
import frostscape.ui.dialog.settings.ControllerSettingDialog;
import frostscape.ui.frag.BlockSelectFrag;
import mindustry.Vars;
import mindustry.game.Rules;
import mindustry.gen.Groups;
import mindustry.gen.Icon;

import static mindustry.Vars.experimental;
import static mindustry.Vars.steam;

/** Contains several dialogs used within the mod **/
public class FrostUI {
    public static FamilyDescriptionDialog family;
    public static ControllerSettingDialog SCHSettings;
    public static BlockSelectFrag select;

    public static void load(){
        family = new FamilyDescriptionDialog("@category.family");
        SCHSettings = new ControllerSettingDialog("@dialog.SCHsettings");
        select = new BlockSelectFrag();
        select.build(Vars.ui.hudGroup);

        modifyUI();
    }

    public static void modifyUI(){

        Dialog menu = Reflect.get(Vars.ui.editor.getClass(), Vars.ui.editor, "menu");

        float swidth = 180f;
        menu.cont.row();
        menu.cont.button("Sector Controller", Icon.hammer, () -> {
            SCHSettings.show(Vars.state.rules, () -> Vars.state.rules = new Rules());
            menu.hide();
        }).padTop(!steam && !experimental ? -3 : 1).size(swidth * 2f + 10, 60f);
    }
}
