package main.world.blocks.defense;

import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;

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
            return false;
        }

        @Override
        public void unitOn(Unit unit){
            if(((enabled && unit.team != team) || overrideEnabled()) && timer(timerDamage, cooldown)){
                triggered();
                damage(tileDamage);
            }
        }

        public void triggered(){

        };
    }
}
