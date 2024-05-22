package dollieson.bcfsadmin.BackEnd.Attacks;


import dollieson.bcfsadmin.BackEnd.Main.Attack;
import dollieson.bcfsadmin.BackEnd.Main.Cock;

public interface AttackModule {
     void apply(Cock Owner, Cock Target, Attack parent);
}
