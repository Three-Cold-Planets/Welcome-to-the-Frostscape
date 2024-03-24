package main.type.weapons;

import arc.math.Angles;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;

public class ThrustSwingWeapon extends SwingWeapon{
    public void updateStaticFields(Unit unit, WeaponMount mount){
        can = unit.elevation > 0;
        lastReload = mount.reload;
        weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation);
        mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y);
        mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
        bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
        bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
        shootAngle = bulletRotation(unit, mount, bulletX, bulletY);
        rotateOverride = true;
    }
    public ThrustSwingWeapon(String name) {
        super(name);
    }

    @Override
    public void updateShooting(Unit unit, WeaponMount mount) {
        mount.shoot = unit.elevation > 0;
        super.updateShooting(unit, mount);
    }

    @Override
    public void updateRotation(Unit unit, WeaponMount mount) {
        super.updateRotation(unit, mount);
    }
}
