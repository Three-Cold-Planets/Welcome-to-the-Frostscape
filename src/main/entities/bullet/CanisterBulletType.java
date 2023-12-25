package main.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import main.math.Math3D;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

//Time delayed bomb after hitting ground when it uses up all bounces.
public class CanisterBulletType extends BouncyBulletType {
    public int charSize = Vars.tilesize;
    public float detonationTime = 35, landingSpeed;
    public boolean stick = true;

    public Effect landEffect = Fx.shootSmokeDisperse;
    public CanisterBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        backColor = Color.white;
        frontColor = Color.white;
        landingSpeed = speed/2;
        visualHeightMin = Layer.effect + 1;
    }

    @Override
    public void updateBouncing(Bullet b) {
        if(!(b.data instanceof Math3D.HeightHolder)) {
            return;
        }
        Math3D.HeightHolder holder = (Math3D.HeightHolder) b.data;
        holder.lift -= gravity * Time.delta;
        holder.height += holder.lift * Time.delta;

        if(holder.height < 0) {
            Log.info(b.fdata);
            if(b.fdata + 1 > bounceCap && bounceCap != -1) {
                b.fdata = 0;
                b.data = null;
                b.lifetime = detonationTime;
                b.time = 0;
                b.vel.clamp(0, landingSpeed);
                landEffect.at(b.x, b.y);
                return;
            }
            bounce(b, holder);
        }
    }

    @Override
    public void hit(Bullet b) {
        super.hit(b);
        Effect.scorch(b.x, b.y, charSize);
    }

    @Override
    public void updateTrailEffects(Bullet b) {
        if(b.data == null) return;
        super.updateTrailEffects(b);
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        Math3D.HeightHolder h = BouncyBulletType.getHolder(b);
        if(fragBullet != null){
            for(int i = 0; i < fragBullets; i++){
                float len = Mathf.random(1f, 7f);
                float a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - fragBullets/2) * fragSpread);
                Bullet bullet = fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
                if(h != null) bullet.data = new Math3D.HeightHolder(h.height, h.lift);
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        if(b.data == null) {
            if(backRegion.found()){
                Draw.color(backColor);
                Draw.rect(backRegion, b.x, b.y, width, height, b.rotation());
            }

            Draw.color(frontColor);
            Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation());

            Draw.reset();
            return;
        }
        super.draw(b);
    }
}
