package frostscape.ui.dialog.database;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.TextField;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.world.Block;

public class ScannedBlocksTable extends Table {
    private TextField search;
    private Table all = new Table(), cont = new Table();
    public ScannedBlocksTable(){
        super();
        add(cont);
        this.all.margin(20.0F).marginTop(0.0F);
        this.cont.table((s) -> {
            s.image(Icon.zoom).padRight(8.0F);
            this.search = s.field(null, (text) -> {
                this.rebuild();
            }).growX().get();
            this.search.setMessageText("@players.search");
        }).fillX().padBottom(4.0F).row();
        this.cont.pane(this.all).scrollX(false);
    }


    public void rebuild(){
        this.all.clear();
        String text = this.search.getText();
        Seq<Content>[] allContent = Vars.content.getContentMap();

        for(int j = 0; j < allContent.length; ++j) {
            ContentType type = ContentType.all[j];
            Seq<Content> array = allContent[j].select((c) -> {
                boolean var10000;
                if (c instanceof UnlockableContent) {
                    UnlockableContent u = (UnlockableContent)c;
                    if ((!u.isHidden() || u.techNode != null) && (text.isEmpty() || u.localizedName.toLowerCase().contains(text.toLowerCase()))) {
                        var10000 = true;
                        return var10000;
                    }
                }

                var10000 = false;
                return var10000;
            });
            if (array.size != 0) {
                this.all.add("@content." + type.name() + ".name").growX().left().color(Pal.accent);
                this.all.row();
                this.all.image().growX().pad(5.0F).padLeft(0.0F).padRight(0.0F).height(3.0F).color(Pal.accent);
                this.all.row();
                this.all.table((list) -> {
                    list.left();
                    int cols = (int) Mathf.clamp(((float) Core.graphics.getWidth() - Scl.scl(30.0F)) / Scl.scl(44.0F), 1.0F, 22.0F);
                    int count = 0;

                    for(int i = 0; i < array.size; ++i) {
                        UnlockableContent unlock;
                        Image image;
                        label45: {
                            label44: {
                                unlock = (UnlockableContent)array.get(i);
                                image = this.unlocked(unlock) ? (new Image(unlock.uiIcon)).setScaling(Scaling.fit) : new Image(Icon.lock, Pal.gray);
                                if (Vars.state.isGame()) {
                                    if (unlock instanceof UnitType) {
                                        UnitType u = (UnitType)unlock;
                                        if (u.isBanned()) {
                                            break label44;
                                        }
                                    }

                                    if (unlock instanceof Block) {
                                        Block b = (Block)unlock;
                                        if (Vars.state.rules.bannedBlocks.contains(b)) {
                                            break label44;
                                        }
                                    }
                                }

                                list.add(image).size(32.0F).pad(3.0F);
                                break label45;
                            }

                            list.stack(new Element[]{image, new Image(Icon.cancel) {
                                {
                                    this.setColor(Color.scarlet);
                                    this.touchable = Touchable.disabled;
                                }
                            }}).size(32.0F).pad(3.0F);
                        }

                        ClickListener listener = new ClickListener();
                        image.addListener(listener);
                        if (!Vars.mobile && this.unlocked(unlock)) {
                            image.addListener(new HandCursorListener());
                            image.update(() -> {
                                image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4F * Time.delta));
                            });
                        }

                        if (this.unlocked(unlock)) {
                            image.clicked(() -> {
                                if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
                                    Core.app.setClipboardText((char)Fonts.getUnicode(unlock.name) + "");
                                    Vars.ui.showInfoFade("@copied");
                                } else {
                                    Vars.ui.content.show(unlock);
                                }

                            });
                            image.addListener(new Tooltip((t) -> {
                                t.background(Tex.button).add(unlock.localizedName + (Core.settings.getBool("console") ? "\n[gray]" + unlock.name : ""));
                            }));
                        }

                        ++count;
                        if (count % cols == 0) {
                            list.row();
                        }
                    }

                }).growX().left().padBottom(10.0F);
                this.all.row();
            }
        }

        if (this.all.getChildren().isEmpty()) {
            this.all.add("@none.found");
        }
    }

    boolean unlocked(UnlockableContent content) {
        return !Vars.state.isCampaign() && !Vars.state.isMenu() || content.unlocked();
    }
}
