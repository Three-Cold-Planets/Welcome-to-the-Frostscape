package frostscape.world.blocks.defense;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Pal;

public class ShockMine extends UpgradeableMine{
    public float damage = 13;
    public int length = 10;
    public int tendrils = 6;
    public Color lightningColor = Pal.lancerLaser;
    public int shots = 6;
    public float inaccuracy = 0f;
    public @Nullable BulletType bullet;

    public ShockMine(String name) {
        super(name);
    }

    public class ShockBuild extends UpgradeableMineBuild{
        @Override
        public void triggered() {
            for(int i = 0; i < tendrils; i++){
                Lightning.create(team, lightningColor, damage * damageMultiplier, x, y, Mathf.random(360f), length);
            }
            if(bullet != null){
                for(int i = 0; i < shots; i++){
                    bullet.create(this, x, y, (360f / shots) * i + Mathf.random(inaccuracy));
                }
            }
            shootSound.at(x, y, Mathf.range(soundMinPitch, soundMaxPitch));
        }
    }
}
