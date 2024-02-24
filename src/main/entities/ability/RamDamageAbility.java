package main.entities.ability;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.*;
import mindustry.world.Tile;

import static arc.math.Mathf.dst2;
import static main.content.FrostBullets.placeholder1;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//Different to MoveDamageLine, used for consistent single instances of damage
public class RamDamageAbility extends Ability {
    static Rect rect = new Rect(), hitrect = new Rect();
    static Vec2 seg1 = new Vec2(), seg2 = new Vec2(), vec = new Vec2();
    static Seq<Healthc> collided = new Seq<>();

    /** Ability damage */
    public float damage = 35f;
    /** Length of the lightning. <= 0 to disable */
    public int length = 12;
    /** Speeds for when to start checking and when to stop getting faster. If maxSpeed is -1, scale infinitely */
    public float minSpeed = 0.8f, maxSpeed = 1.2f;
    /** Shifts where the lightning spawns along the Y axis */
    public float y = 0f;
    /** Offset along the X axis */
    public float x = 0f;

    /** Recoil when hitting things. **/
    public float recoil = 0;

    /** Whether the spawn side alternates */
    public boolean alternate = false;
    /** Whether ground/air are affected units */
    public boolean hitGround, hitAir;
    /** Bullet angle parameters */
    public float angleOffset = 0f;
    /** Effect spawned upon bullet creation **/
    public Effect effect,
    /** Effect for hitting things. Self explanitory **/
    hitEffect;

    //How much additional armor is applied on collisions
    public int armor = 0;

    protected float side = 1f;
    protected float reload = 1;


    public RamDamageAbility(float damage, int length, float x, float y, float minSpeed, float maxSpeed,  float angleOffset, int armor, boolean hitGround, boolean hitAir, Effect effect, Effect hitEffect){
        this.damage = damage;
        this.length = length;
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.armor = armor;
        this.hitGround = hitGround;
        this.hitAir = hitAir;
        this.effect = effect;
        this.hitEffect = hitEffect;
    }
    public RamDamageAbility(float damage, int length, float x, float y, float minSpeed, float maxSpeed,  float angleOffset, float recoil, int armor, boolean hitGround, boolean hitAir, Effect effect, Effect hitEffect){
        this.damage = damage;
        this.length = length;
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.recoil = recoil;
        this.armor = armor;
        this.hitGround = hitGround;
        this.hitAir = hitAir;
        this.effect = effect;
        this.hitEffect = hitEffect;
    }

    @Override
    public void update(Unit unit){
        reload = Mathf.approach(reload, 2, Time.delta);
        if(reload < 2 || unit.vel.len() < minSpeed) return;

        float scl = maxSpeed == -1 ? Mathf.clamp(unit.vel.len() - minSpeed)/minSpeed : Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        float angle = unit.rotation + angleOffset;
        float x = Angles.trnsx(unit.rotation - 90, this.x, this.y) + unit.x;
        float y = Angles.trnsy(unit.rotation - 90, this.x, this.y) + unit.y;


        //Because I had to find if I needed recoil
        //I find this *very* painful

        vec.trnsExact(angle, length);

        if(hitGround){
            seg1.set(x, y);
            seg2.set(seg1).add(vec);
            World.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                boolean collide = tile != null && tile.team != unit.team && !tile.block.underBullets && tile.block.solid;
                if(collide){
                    collided.add(tile);

                    for(Point2 p : Geometry.d4){
                        Tile other = world.tile(p.x + cx, p.y + cy);
                        if(other != null && (Intersector.intersectSegmentRectangle(seg1, seg2, other.getBounds(Tmp.r1)))){
                            Building build = other.build;
                            if(build != null && build.team != unit.team && !tile.block.underBullets && tile.block.solid){
                                collided.add(build);
                            }
                        }
                    }
                }
                return false;
            });
        }

        float expand = 3f;

        rect.setPosition(x, y).setSize(vec.x, vec.y).normalize().grow(expand * 2f);
        float x2 = vec.x + x, y2 = vec.y + y;

        Units.nearbyEnemies(unit.team, rect, u -> {
            if(u.checkTarget(hitAir, hitGround) && u.hittable()){
                u.hitbox(hitrect);

                Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

                if(vec != null){
                    collided.add(u);
                }
            }
        });

        collided.sort(c -> dst2(c.x(), c.y()));
        if(collided.size == 0) return;
        Healthc entity = collided.get(0);
        float angleTo = vec.set(x, y).angleTo(entity.x(), entity.y());
        float accuracy = 1 - Angles.angleDist(angleTo, angle)/180;
        entity.damage(damage * accuracy);
        //With every force be an equal and opposite reaction and yada yada
        unit.armor += armor;
        unit.damage(damage * accuracy);
        unit.armor -= armor;
        hitEffect.at(x, y, angle);
        unit.vel.add(Tmp.v1.trns(angle + 180, recoil * scl));
        if(entity instanceof Unit hitUnit) hitUnit.vel.add(Tmp.v1.trns(angle, recoil * scl));
        collided.clear();
        reload = 0;
    }
}
