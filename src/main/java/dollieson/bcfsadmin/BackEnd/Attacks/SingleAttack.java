package dollieson.bcfsadmin.BackEnd.Attacks;

import dollieson.bcfsadmin.BackEnd.Main.Attack;
import dollieson.bcfsadmin.BackEnd.Main.Cock;

public class SingleAttack implements AttackModule{
    @Override
    public void apply(Cock owner, Cock entity, Attack parent) {
        int damage = parent.getDamage() + (int) (owner.getStat(Cock.StatName.DAMAGE, Cock.StatType.CURRENT) * parent.getDamageMultiplier());
        entity.addStat(Cock.StatName.HEALTH, Cock.StatType.BONUS,damage * -1);
    }
}
