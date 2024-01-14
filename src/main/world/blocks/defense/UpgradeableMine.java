package main.world.blocks.defense;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.io.Reads;
import main.content.FrostUpgrades;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import main.world.systems.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class UpgradeableMine extends BaseBlock {
    public final int timerDamage = timers++;

    public float cooldown = 80f;
    public float tileDamage = 5f;
    public float teamAlpha = 0.3f;
    public Sound shootSound = Sounds.spark;
    public float soundMinPitch = 0.8f, soundMaxPitch = 1.1f;
    public DrawBlock drawer;

    public boolean destroyableDerelict;

    @Override
    public boolean canBreak(Tile tile) {
        return super.canBreak(tile) && (destroyableDerelict || tile.build.team != Team.derelict || Vars.state.rules.infiniteResources);
    }

    public UpgradeableMine(String name) {
        super(name);
        drawer = new DrawDefault();
        destructible = true;
        solid = false;
        destroyableDerelict = false;
        targetable = false;
        hasShadow = false;
        underBullets = true;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    public class UpgradeableMineBuild extends BaseBuilding {
        public boolean alwaysArmed = false;
        //note that if the mine is instant, set warmup to 1 and decrease it.
        public float warmup = 0;

        @Override
        public void drawTeam(){
            //no
        }

        @Override
        public void draw(){
            drawer.draw(this);
            Draw.color(team.color, teamAlpha);
            Draw.rect(teamRegion, x, y);
            Draw.color();
        }

        @Override
        public void drawCracks(){
            //no
        }

        public boolean overrideEnabled(){
            return upgrades.getState(FrostUpgrades.alwaysArmed).installed;
        }

        public boolean canTarget(Unit unit){
            return (enabled && unit.team != team) || overrideEnabled();
        }

        @Override
        public void unitOn(Unit unit){
            if(canTarget(unit) && timer(timerDamage, cooldown)){
                triggered();
                damage(tileDamage);
            }
        }

        public void triggered(){
            shootSound.at(x, y, Mathf.random(soundMinPitch, soundMaxPitch));
        };

        public boolean activated(){
            return !Mathf.zero(warmup);
        }

        @Override
        public void display(Table table) {
            boolean allied = this.team == Vars.player.team();
            boolean visible = activated() || allied;
            table.table((t) -> {
                t.left();
                t.add(visible ? new Image(this.block.getDisplayIcon(this.tile)) : new Image(this.tile.floor().uiIcon)).size(32.0F);
                t.labelWrap(() -> this.team == Vars.player.team() || activated() ? this.getDisplayName() : this.tile.floor().localizedName + "..?").left().width(190.0F).padLeft(5.0F);
            }).growX().left();
            table.row();
            if (allied) {
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

            if (Vars.net.active() && this.lastAccessed != null) {
                table.row();
                table.add(Core.bundle.format("lastaccessed", new Object[]{this.lastAccessed})).growX().wrap().left();
            }

            table.marginBottom(-5.0F);
        }

        //Having this as base behaviour, since I doubt you'll make a mine without it
        @Override
        public void upgraded(UpgradeState state) {
            super.upgraded(state);
            if(state.upgrade == FrostUpgrades.alwaysArmed) alwaysArmed = true;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            alwaysArmed = upgrades.getState(FrostUpgrades.alwaysArmed).level > 0;
        }
    }
}
