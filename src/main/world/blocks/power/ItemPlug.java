package main.world.blocks.power;

import arc.Core;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import main.world.blocks.PlugBlock;
import main.world.systems.bank.ResourceBankHandler;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

// Tries to keep its inventory at the target stock. Imports/exports accordingly.
public class ItemPlug extends PlugBlock {

    //Max items it can pull at once
    public float maxExchanged = 4;


    public ItemPlug(String name) {
        super(name);
        group = BlockGroup.transportation;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        hasItems = true;
        itemCapacity = 10;
        this.config(Item.class, (build, item) -> {
            ((ItemPlugBuild) build).stockItem = item;
        });
        this.configClear((build) -> {
            ((ItemPlugBuild) build).stockItem = null;
        });
    }

    public class ItemPlugBuild extends PlugBuild{

        //Percent of item capacity to keep full.
        public float target = 0.5f;

        //Percent margin on either side of the target.
        public float margin = 0.1f;

        public float exchangeTime = 0;

        public int id;

        public Item stockItem;
        public BusState state = BusState.stable;
        public float time = 0;

        @Override
        public void exchange() {
            super.exchange();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return this.team == source.team && stockItem == item && this.items.total() < itemCapacity && ResourceBankHandler.itemCap > 0;
        }

        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(block, table, Vars.content.items(), () -> stockItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public void display(Table table) {
            table.table((t) -> {
                t.left();
                t.add(new Image(this.block.getDisplayIcon(this.tile))).size(32.0F);
                t.labelWrap(this.block.getDisplayName(this.tile)).left().width(190.0F).padLeft(5.0F);
            }).growX().left();
            table.row();

            if (this.team == Vars.player.team()){
                table.table((bars) -> {
                    bars.defaults().growX().height(18.0F).pad(4.0F);
                    this.displayBars(bars);
                }).growX();
                table.row();

                table.label(() -> Core.bundle.format("bar.plugefficiency", (int)(sum/size/size * 100))).growX().left().padTop(5);
                table.row();

                table.table(tab -> {
                    tab.left();
                    tab.table(t -> {
                        t.left();
                        t.image().update(i -> {
                            i.setDrawable(
                                    switch (state){
                                        case stable -> Icon.cancel;
                                        case exporting -> Icon.download;
                                        case importing -> Icon.export;
                                        case empty -> Icon.warning;
                                        case full -> Icon.downOpen;
                                        default -> Icon.exit;
                                    }
                            );
                            i.setScaling(Scaling.fit);
                        }).size(32).padBottom(-4).padRight(2);
                    }).left();
                    tab.table(t -> {
                        t.right();

                        t.label(() -> {
                                    if(state == PowerPlug.BusState.stable || state == PowerPlug.BusState.full || state == PowerPlug.BusState.empty || state == PowerPlug.BusState.disabled) return Core.bundle.get(state.name);
                                    return Core.bundle.format(state.name, Strings.fixed(maxExchanged * 60.0f * efficiency * this.timeScale(), 1));
                                }
                        );
                        t.add();
                    }).right();
                }).growX().left().padTop(5);
            }

            if (Vars.net.active() && this.lastAccessed != null) {
                table.row();
                table.add(Core.bundle.format("lastaccessed", new Object[]{this.lastAccessed})).growX().wrap().left();
            }

            table.marginBottom(-5.0F);
        }
    }
}
