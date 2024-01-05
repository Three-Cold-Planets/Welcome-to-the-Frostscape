package main.world.blocks.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import main.content.FrostUpgrades;
import main.world.systems.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Icon;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Tile;

public class ThermalMine extends UpgradeableMine{

    protected static boolean unitFound;
    public float radius = 24, lightMulti = 0.95f;

    public Effect heatEffect;
    public float effectInterval = -1;
    public Color explosionCenter = Pal.lightPyraFlame, explosionEdge = Pal.darkPyraFlame;
    public float activationTime = 150;
    public float warmupSpeed = 0.2f, warmDownSpeed = 0.15f;

    public StatusEffect status = StatusEffects.melting;
    public float statusDuration = 100;
    public float damage = 20;

    public ThermalMine(String name) {
        super(name);
        cooldown = 15;
        tileDamage = 2;
        heatEffect = Fx.fire;
    }

    @Override
    public void init() {
        super.init();
        if(effectInterval == -1) effectInterval = heatEffect.lifetime;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("charge", entity -> new Bar(() -> Core.bundle.get("stat.charge"), () -> Tmp.c1.set(explosionCenter).lerp(explosionEdge, entity.warmup()), entity::warmup).blink(Color.white));
    }

    //Hidden.
    @Override
    public int minimapColor(Tile tile){
        return tile.floor().mapColor.rgba();
    }

    public class ThermalMineBuild extends UpgradeableMineBuild{
        public float warmup = 0, heatRadius = 0, effectTimer, active;
        public boolean alwaysArmed = false;

        @Override
        public void draw() {
            Draw.alpha(warmup);
            super.draw();
            Draw.blend(Blending.additive);
            Draw.z(Layer.flyingUnitLow);
            Fill.light(x, y, (int) (heatRadius * lightMulti/4) + 8, heatRadius * lightMulti, Tmp.c1.set(explosionCenter).a(warmup), Tmp.c2.set(explosionEdge).a(0));
            Draw.blend();
            if(tile.floor().emitLight) tile.floor().drawEnvironmentLight(tile);
        }

        public float getRadius(){
            return radius * rangeMultiplier;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public boolean overrideEnabled() {
            return upgrades.getState(FrostUpgrades.alwaysArmed).installed;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            boolean activated = activated();

            warmup = Mathf.approach(warmup, activated ? 1 : 0, activated ? warmupSpeed * speedMultiplier : warmDownSpeed * speedMultiplier);
            heatRadius = Mathf.approach(heatRadius, activated ? getRadius() : 0, warmup);
            if(warmup < 0.001F) heatRadius = 0;

            if(activated) {
                if(effectTimer++ >= effectInterval) {
                    effectTimer %= effectInterval;
                    heatEffect.at(x, y);
                }

                unitFound = false;
                boolean time = timer(timerDamage, cooldown/reloadMultiplier);
                Units.nearby(x - heatRadius, y - heatRadius, heatRadius * 2, heatRadius * 2, u -> {
                    float dst = u.dst(this);
                    if(dst > heatRadius) return;
                    unitFound = true;
                    if(!time) return;
                    u.apply(status, statusDuration);
                    u.damagePierce(damage * (1 - dst/heatRadius) * damageMultiplier, true);
                });
                if(time) damage(tileDamage);
                if(!unitFound) active -= Time.delta;
                else active = activationTime;
            }
        }


        public boolean activated(){
            return active > 0.001F;
        }

        @Override
        public void display(Table table) {
            boolean visible = this.team == Vars.player.team() || activated();
            table.table((t) -> {
                t.left();
                t.add(visible ? new Image(this.block.getDisplayIcon(this.tile)) : new Image(this.tile.floor().uiIcon)).size(32.0F);
                t.labelWrap(() -> this.team == Vars.player.team() || activated() ? this.getDisplayName() : this.tile.floor().localizedName + "..?").left().width(190.0F).padLeft(5.0F);
            }).growX().left();
            table.row();
            if (visible) {
                table.table((bars) -> {
                    bars.defaults().growX().height(18.0F).pad(4.0F);
                    this.displayBars(bars);
                }).growX();

                table.row();
                table.table(t -> {
                    t.left();
                    t.image().update(i -> {
                        i.setDrawable(activated() ? Icon.eyeSmall : Icon.eyeOffSmall);
                        i.setScaling(Scaling.fit);
                    }).size(32).padBottom(-4).padRight(2);
                }).left();
            }
        }

        @Override
        public void triggered() {
            super.triggered();
            active = activationTime;
        }

        @Override
        public void drawDisabled() {

        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            alwaysArmed = upgrades.getState(FrostUpgrades.alwaysArmed).level > 0;
        }

        @Override
        public void upgraded(UpgradeState state) {
            super.upgraded(state);
            if(state.upgrade == FrostUpgrades.alwaysArmed) alwaysArmed = true;
        }
    }
}
