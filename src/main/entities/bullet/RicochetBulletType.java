package main.entities.bullet;

import arc.math.geom.Intersector;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;

public class RicochetBulletType extends BasicBulletType {
    //how much of its velocity is kept on bounce
    public float bounciness = 1;
    //Cap for how many times it can bounce. Set to -1 to disable, 0 or null to stop bouncing.
    public int bounceCap = -1;
    public boolean bounceUnits, bounceBuildings;
    //Effect displayed on bounce
    public Effect bounceEffect = Fx.plasticExplosion;

    //Whether the bullet can bounce multiple times on the same entity
    public boolean bounceSame = true;

    //how many ticks inbetween collisions being cleared. Set around 15/20 or lower to make bullet reliable in low fps. -1 to disable
    public int bounceInternal = -1;

    public boolean useRange = true;

    public static class Bounces{
        public int bounceAmount = 0;
    }

    public RicochetBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        this.speed = speed;
        this.damage = damage;
        this.pierceBuilding = this.pierce = this.bounceBuildings = this.bounceUnits = true;
        this.trailWidth = width * 0.38f;
        this.shrinkX = 0.8f;
    }

    @Override
    public void update(Bullet b){
        if(bounceInternal != -1 && b.timer.get(1, bounceInternal)){{
            b.collided.clear();
        }}
        super.update(b);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        b.fdata++;
        if ((bounceCap == -1 || b.fdata <= bounceCap) && bounceUnits) {
            if(b.collided.size >= 1) b.collided.pop();
            if((entity instanceof Unit)) {
            Unit unit = (Unit) entity;
                //Bring bullet back to point of intersection
                float rotation = b.vel.angle();
                b.vel.setAngle(360 + rotation - (rotation - b.angleTo(entity)) * 2);
                b.vel.rotate(180);
                Tmp.v1.trns(b.angleTo(unit.x, unit.y) + 180, unit.hitSize).add(unit.x, unit.y).add(b.vel);

                b.x = Tmp.v1.x;
                b.y = Tmp.v1.y;
                b.vel.add(((Unit) entity).vel());
                bounceEffect.at(b.x, b.y, b.vel.angle());
            }
        }
        super.hitEntity(b, entity, health);
    }


    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        b.fdata++;
        if(build != null){
            float difX = b.x - build.x,
            difY = b.y - build.y;
            boolean flipX = false;
            boolean flipY = true;
            if(Math.abs(difX) > Math.abs(difY)) {
                flipX = true;
                flipY = false;
            }
            if ((bounceCap == -1 || b.fdata <= bounceCap) && bounceBuildings) {
                //Flip velocity after sliding back. I swear I canot tell how anoying this was to figure out.

                //Offset from the middle to it's sides
                float size = build.block.size * Vars.tilesize/2;

                //Corners of side
                Tmp.v1.set(size, 0);

                //Set theese two vectors to corners on the right side
                Tmp.v2.set(Tmp.v1).add(0, size);
                Tmp.v1.sub(0, size);

                //Rotate apropriately
                if(flipX && difX < 0){
                    Tmp.v2.scl(-1);
                    Tmp.v1.scl(-1);
                }
                else if (flipY){
                    //Set bottom right corner to top left
                    Tmp.v1.set(-size, size);
                    if(difY < 0){
                        //Flip the corners across the middle, so that they're on bottom
                        Tmp.v1.scl(-1);
                        Tmp.v2.scl(-1);
                    }
                }
                Tmp.v1.add(build.x, build.y);
                Tmp.v2.add(build.x, build.y);
                Tmp.v5.set(b.x, b.y);
                Intersector.intersectLines(Tmp.v1, Tmp.v2, Tmp.v3.set(b), Tmp.v4.trns(b.vel.angle() + 180, size * 2).add(b.x, b.y), Tmp.v5);
                b.x = Tmp.v5.x;
                b.y = Tmp.v5.y;

                if (flipX) {
                    b.vel.x *= -1 * bounciness;
                }
                if (flipY) {
                    b.vel.y *= -1 * bounciness;
                }
                b.x += b.vel.x;
                b.y += b.vel.y;
                bounceEffect.at(Tmp.v5.x, Tmp.v5.y, b.vel.angle());
            }
        }
        super.hitTile(b, build, x, y, initialHealth, direct);
    }
}
