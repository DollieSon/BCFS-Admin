package dollieson.bcfsadmin.BackEnd.Attacks;


import dollieson.bcfsadmin.BackEnd.Globals.Attack;
import dollieson.bcfsadmin.BackEnd.Globals.Cock;

public interface AttackModule {
     void apply(Cock Owner, Cock Target, Attack parent);
}
