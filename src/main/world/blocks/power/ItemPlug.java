package main.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import main.world.blocks.PlugBlock;
import main.world.systems.bank.ResourceBankHandler;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

// Tries to keep its inventory at the target stock. Imports/exports accordingly.
public class ItemPlug extends PlugBlock {

    //Max items it can pull at once
    public int maxExchanged = 1;

    public float pullTime = 15;


    public ItemPlug(String name) {
        super(name);
        group = BlockGroup.transportation;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        hasItems = true;
        itemCapacity = 50;
        this.config(Item.class, (build, item) -> {
            ((ItemPlugBuild) build).stockItem = item;
        });
        this.configClear((build) -> {
            ((ItemPlugBuild) build).stockItem = null;
        });
    }

    @Override
    public void setBars() {
        super.setBars();
        this.addBar("bank-items", (entity) -> {
            ItemPlugBuild plug = (ItemPlugBuild) entity;
            return new Bar(() -> {
                return Core.bundle.format("bar.bank.itemamount", plug.stockItem == null || Float.isNaN(ResourceBankHandler.items.get(plug.stockItem)) ? "<ERROR>" : UI.formatAmount((long)((int)(ResourceBankHandler.items.get(plug.stockItem)))));
            }, () -> {
                return plug.stockItem == null ? Color.red : plug.stockItem.color;
            }, () -> {
                return plug.stockItem == null ? 1 : ((float) ResourceBankHandler.items.get(plug.stockItem))/ResourceBankHandler.itemCap;
            });
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
        public float percent = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(timer(timerDump, dumpTime / timeScale)){
                dump();
                dump(stockItem);
            }
            exchangeTime = Mathf.approach(exchangeTime, pullTime, edelta());
            if(exchangeTime >= pullTime){
                exchangeTime = Mathf.mod(exchangeTime, pullTime);
                if(state == BusState.exporting){
                    ResourceBankHandler.items.add(stockItem, maxExchanged);
                    items.remove(stockItem, maxExchanged);
                }
                if(state == BusState.importing){
                    items.add(stockItem, maxExchanged);
                    ResourceBankHandler.items.remove(stockItem, maxExchanged);
                }
            }
        }

        @Override
        public void exchange() {
            if(stockItem == null){
                state = BusState.stable;
                time = 0;
                return;
            }
            if(ResourceBankHandler.itemCap == 0){
                state = BusState.disabled;
                return;
            }

            if(ResourceBankHandler.building.items.get(stockItem) >= ResourceBankHandler.itemCap){
                state = BusState.full;
                time = 0;
                return;
            }

            percent = ((float) items.get(stockItem)) / itemCapacity;
            if(Mathf.within(percent, target, margin)){
                state = BusState.stable;
                return;
            }

            if(percent > target + margin){
                active = true;

                if(time < exchangeDelay){
                    state = BusState.startingExport;
                    time += Time.delta;
                    return;
                }
                state = BusState.exporting;
                return;
            }

            //import check
            if(percent < target - margin){
                if(ResourceBankHandler.building.items.get(stockItem) == 0){
                    state = BusState.empty;
                    return;
                }

                active = true;

                if(time < exchangeDelay){
                    state = BusState.startingImport;
                    time += Time.delta;
                    return;
                }
                state = BusState.importing;
                return;
            }

            state = BusState.stable;
            time = 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return this.team == source.team && stockItem == item && this.items.total() < itemCapacity && ResourceBankHandler.itemCap > 0;
        }

        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(block, table, Vars.content.items(), () -> stockItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public void draw() {
            super.draw();
            Lines.stroke(2);
            Draw.color(Color.white);
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
                                    return Core.bundle.format(state.name, Strings.fixed(maxExchanged * 60/pullTime * efficiency * timeScale(), 1));
                                }
                        );
                        t.add();
                    }).right();
                }).growX().left().padTop(5);
            }

            table.row();
            Image icon = new Image();
            icon.update(() -> {
                if(stockItem == null) {
                    icon.setDrawable(Icon.cancel);
                    return;
                }
                icon.setDrawable(stockItem.fullIcon);
            });
            table.add(icon).size(32, 32);

            if (Vars.net.active() && this.lastAccessed != null) {
                table.row();
                table.add(Core.bundle.format("lastaccessed", new Object[]{this.lastAccessed})).growX().wrap().left();
            }

            table.marginBottom(-5.0F);
        }
    }
}
