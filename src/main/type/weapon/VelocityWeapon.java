package main.type.weapon;

import arc.math.Mathf;
import arc.util.Time;
import main.util.VelUtils;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;

/**Reloads based on how fast the owner is moving.*/
public class VelocityWeapon extends BaseWeapon {
    public float from,
            to,
            threshold,
            target;
    public VelocityWeapon(String name){
        this.name = name;
    }

    public VelocityWeapon(){
        this("");
    }

    @Override
    public void updateReload(Unit unit, WeaponMount mount) {
        float diff = VelUtils.direction(unit.vel, unit.rotation);
        float activation = Mathf.lerp(from, to, Mathf.clamp(Mathf.maxZero(unit.vel().len2() - threshold) / target, 0, 1)) * diff;

        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier * activation, 0);
        mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);
        mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;
    }
}
