package main.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import main.content.FrostBlocks;
import main.content.Fxf;
import main.world.blocks.PlugBlock;
import main.world.systems.bank.ResourceBankHandler;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

import static mindustry.world.blocks.power.PowerNode.makeBatteryBalance;

//Basically a battery which adjusts its own power level
public class PowerPlug extends PlugBlock {

    public float maxExchanged = 4;
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
        lightColor = Color.white.cpy().a(0.25f);
        lightRadius = 34;
        workEffect = Fxf.powerSpark;
    }

    @Override
    public void setBars() {
        super.setBars();
        this.addBar("batteries", makeBatteryBalance());
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

    public class PowerPlugBuild extends PlugBuild{
        public BusState state = BusState.stable;
        public float lastExchanged;
        public float lastStatus;

        public float time = 0;

        @Override
        public void exchange(){
            if(ResourceBankHandler.powerCap == 0) {
                state = BusState.disabled;
                return;
            }

            float storage = power.graph.getTotalBatteryCapacity();
            float status = power.graph.getBatteryStored()/storage;
            efficiency = sum/size/size * Math.abs(status * 2 - 1);
            lastStatus = status;
            float powerRemoved = maxExchanged * edelta();

            if(status > 0.6f){
                //max amount that can be put into the bank
                float reserved = Math.max(ResourceBankHandler.powerCap * ResourceBankHandler.power.status + powerRemoved - ResourceBankHandler.powerCap, 0);
                powerRemoved = Math.max(powerRemoved - reserved, 0);

                if(Mathf.within(powerRemoved, 0, 0.01f)) {
                    state = BusState.full;
                    return;
                }

                active = true;
                if(time < exchangeDelay){
                    state = BusState.startingExport;
                    time += Time.delta;
                    return;
                }

                power.graph.transferPower(-powerRemoved);
                ResourceBankHandler.power.graph.transferPower(powerRemoved);
                state = BusState.exporting;
                lastExchanged = powerRemoved;
                return;
            }
            if(status < 0.4f){
                //max amount that can be drawn from the bank
                float remaining = Math.max(ResourceBankHandler.powerCap * ResourceBankHandler.power.status, 0);
                //Cap power removed by total amount remaining
                powerRemoved = Math.min(powerRemoved, remaining);

                if(Mathf.within(powerRemoved, 0, 0.01f)) {
                    state = BusState.empty;
                    return;
                }

                active = true;
                if(time < exchangeDelay){
                    state = BusState.startingImport;
                    time += Time.delta;
                    return;
                }

                power.graph.transferPower(powerRemoved);
                ResourceBankHandler.power.graph.transferPower(-powerRemoved);
                state = BusState.importing;
                lastExchanged = powerRemoved;
                return;
            }
            state = BusState.stable;
            lastExchanged = 0;
            time = 0;
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
                                        case startingExport, exporting -> Icon.download;
                                        case startingImport, importing -> Icon.export;
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
                                    if(state == BusState.stable || state == BusState.full || state == BusState.empty || state == BusState.disabled) return Core.bundle.get(state.name);
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