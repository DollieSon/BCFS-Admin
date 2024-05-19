package dollieson.bcfsadmin.BackEnd.Attacks;


import dollieson.bcfsadmin.BackEnd.Globals.Attack;
import dollieson.bcfsadmin.BackEnd.Globals.Cock;

public class Leech implements AttackModule{

    public void apply(Cock owner, Cock target, Attack parent) {
        int damage = parent.getDamage() + (int) (owner.getStat(Cock.StatName.DAMAGE, Cock.StatType.CURRENT) * parent.getDamageMultiplier());
        int newHealth = target.getStat(Cock.StatName.HEALTH, Cock.StatType.BONUS) - damage;
        //Apply Damage
        target.setStat(Cock.StatName.HEALTH, Cock.StatType.BONUS,newHealth);
        // Heal Owner
        owner.setStat(Cock.StatName.HEALTH, Cock.StatType.BONUS,damage);
    }


}
