package frostscape.ui.dialog;

import arc.Core;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import mindustry.content.Blocks;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static frostscape.Frostscape.NAME;

public class DatabaseEntriesDialog extends BaseDialog {

    public Table contents = new Table();
    public Table buttonList = new Table();
    public float padding = 150;
    public DatabaseEntriesDialog(String name){
        super(name);

        buttons.left();
        addCloseButton();

        contents.background(Tex.button);
        cont.add(contents).center().size(Core.graphics.getWidth() - 30, Core.graphics.getHeight() - buttons.getHeight() - padding);
        buttons.background(Tex.button);
        buttons.image().fillY().color(Pal.darkerGray).width(5);
        buttons.pane(buttonList).width(Core.graphics.getWidth() - 240).style(Styles.horizontalPane).left();
        buttonList.fill().left();
        defaultSetup();

        addButton("@about.button", Icon.info, this::defaultSetup);

        addButton("@category.family", Core.atlas.drawable(NAME + "-hunter"), () -> {
            contents.clear();
        });

        addButton("@category.scanning", Core.atlas.drawable(Blocks.radar.name), () -> {
            contents.clear();
        });
    }

    @Override
    public void addCloseButton() {
        this.buttons.defaults().size(210.0F, 64.0F);
        this.buttons.button("@back", Icon.left, this::hide).size(210.0F, 64.0F);
        this.addCloseListener();
    }

    public void addButton(String name, Drawable icon, Runnable func){
        this.buttonList.button(name, icon, func).size(210.0F, 64.0F);
    }

    public void defaultSetup(){
        contents.clear();
        contents.pane(pane -> {
            pane.add("@dialog.database-entries.info0");
        });
    }
}
