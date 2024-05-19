package dollieson.bcfsadmin.BackEnd.Builders;


import dollieson.bcfsadmin.BackEnd.Attacks.AttackModule;
import dollieson.bcfsadmin.BackEnd.Attacks.Heal;
import dollieson.bcfsadmin.BackEnd.Attacks.Leech;
import dollieson.bcfsadmin.BackEnd.Attacks.SingleAttack;

public class AttackModuleBuilder {
    public static AttackModule buildAttackModule(int type){
        AttackModule res;
        switch (type){
            case 1:
                res = new SingleAttack();
                break;
            case 2:
                res = new Leech();
                break;
            case 3:
                res = new Heal();
                break;
            default:
                throw new IllegalStateException("attack module not found");
        }
        return res;
    }
}
