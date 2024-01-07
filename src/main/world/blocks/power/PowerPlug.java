package main.world.blocks.power;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Scaling;
import main.content.FrostBlocks;
import main.world.BaseBuilding;
import main.world.blocks.PlugBlock;
import main.world.systems.bank.ResourceBankHandler;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

//Basically a battery which adjusts its own power level
public class PowerPlug extends PlugBlock {

    public float maxExchanged = 500/60;
    public PowerPlug(String name) {
        super(name);
        solid = true;
        hasPower = true;
        group = BlockGroup.power;
        outputsPower = true;
        consumesPower = true;
        canOverdrive = false;
        flags = EnumSet.of(new BlockFlag[]{BlockFlag.battery});
        envEnabled |= 2;
        destructible = true;
        update = true;
        validFloors = Seq.with(FrostBlocks.powerSocket, FrostBlocks.powerSocketLarge);
    }

    @Override
    public void setBars() {
        super.setBars();
        this.addBar("bank-power", (entity) -> {
            return new Bar(() -> {
                return Core.bundle.format("bar.bank.poweramount", new Object[]{Float.isNaN(ResourceBankHandler.power.status * ResourceBankHandler.powerCap) ? "<ERROR>" : UI.formatAmount((long)((int)(ResourceBankHandler.power.status * ResourceBankHandler.powerCap)))});
            }, () -> {
                return Pal.powerBar;
            }, () -> {
                return ResourceBankHandler.power.status;
            });
        });
    }

    public class PowerPlugBuild extends BaseBuilding{
        public BusState state;
        public float lastExchanged;
        @Override
        public void updateTile() {
            super.updateTile();

            float storage = power.graph.getTotalBatteryCapacity();
            float status = power.graph.getBatteryStored()/storage;
            efficiency = Mathf.clamp(Math.abs(status - 0.5f) * 2);
            float powerRemoved = maxExchanged * edelta();

            if(status > 0.55f){
                //max amount that can be put into the bank
                float reserved = Math.max(powerRemoved * ResourceBankHandler.power.status - ResourceBankHandler.powerCap, 0);
                powerRemoved = Math.max(powerRemoved - reserved, 0);

                power.graph.transferPower(-powerRemoved);
                ResourceBankHandler.power.graph.transferPower(powerRemoved);
                state = BusState.exporting;
                lastExchanged = powerRemoved;
                return;
            }
            if(status < 0.45f && !Mathf.zero(ResourceBankHandler.power.status)){
                //max amount that can be drawn from the bank
                float remaining = Math.max(ResourceBankHandler.powerCap * ResourceBankHandler.power.status, 0);
                //Cap power removed by total amount remaining
                powerRemoved = Math.min(powerRemoved, remaining);

                power.graph.transferPower(powerRemoved);
                ResourceBankHandler.power.graph.transferPower(-powerRemoved);
                state = BusState.importing;
                lastExchanged = powerRemoved;
                return;
            }
            state = BusState.stable;
            lastExchanged = 0;
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
                                    }
                            );
                            i.setScaling(Scaling.fit);
                        }).size(32).padBottom(-4).padRight(2);
                    }).left();
                    tab.table(t -> {
                        t.right();

                        t.label(() -> {
                                    if(state == BusState.stable) return Core.bundle.get(state.name);
                                    return Core.bundle.format(state.name, UI.formatAmount((int)(lastExchanged * 60)));
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

    private enum BusState{
        importing("bar.importing"),
        exporting("bar.exporting"),
        stable("bar.stable");

        final String name;

        BusState(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}