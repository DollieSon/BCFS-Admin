package dollieson.bcfsadmin.BackEnd.Globals;


import dollieson.bcfsadmin.BackEnd.Main.Attack;
import dollieson.bcfsadmin.BackEnd.Main.Cock;
import dollieson.bcfsadmin.BackEnd.Main.MatchFacade;
import dollieson.bcfsadmin.BackEnd.Threading.MotherThreadController;

import java.util.ArrayList;
import java.util.HashMap;

public class Helpers {
    public static int Fight(Cock player, Cock enemy){
        int rounds = 0;
        //reset Player Health
        player.resetCurrent();
        //reset Enemy Health
        player.resetCurrent();
        while(true) {
            if(player.getStat(Cock.StatName.HEALTH, Cock.StatType.CURRENT) <= 0){
                // System.out.println("Player Died");
                //System.out.println("Rounds:" + rounds);
                return enemy.getCockID();
            }
            if(enemy.getStat(Cock.StatName.HEALTH, Cock.StatType.CURRENT) <= 0){
                // System.out.println("Enemy Died");
                //System.out.println("Rounds:" + rounds);
                return player.getCockID();
            }
            Attack PlayerAttack = player.getAttack();
            Attack EnemyAttack = enemy.getAttack();
            int player_attackSpeed = PlayerAttack.getCurrSpeed() + player.getStat(Cock.StatName.SPEED, Cock.StatType.CURRENT);
            int entity_attackSpeed = EnemyAttack.getCurrSpeed() + enemy.getStat(Cock.StatName.SPEED, Cock.StatType.CURRENT);
            if(player_attackSpeed <= entity_attackSpeed){
                // System.out.println(player.getName() + "Attacks with " + PlayerAttack.getName());
                PlayerAttack.apply(player,enemy);
                if(rounds > 75) player.addStat(Cock.StatName.HEALTH, Cock.StatType.BONUS,-(rounds * rounds/2));
            }else{
                // System.out.println(enemy.getName() + "Attacks with " + EnemyAttack.getName());
                EnemyAttack.apply(enemy,player);
                if(rounds > 75) enemy.addStat(Cock.StatName.HEALTH, Cock.StatType.BONUS,-(rounds * rounds/2));
            }
            // System.out.println(player.toString());
            // System.out.println(enemy.toString());
            rounds++;
        }
    }
    public static void addIfNotExist(HashMap<Integer,int[]> battleRep,int key,int ind,int val){
        if(!battleRep.containsKey(key)){
            battleRep.put(key,new int[2]);
        }
        int[] winloss = battleRep.get(key);
        winloss[ind] += val;
    }

    public static HashMap<Integer,int[]> allVersusAll(HashMap<Integer,Cock> AllCock){
        HashMap<Integer,Cock> allC= AllCock;
        ArrayList<Integer> nIds = new ArrayList<>(allC.keySet());
        int x = nIds.size();
        ArrayList<MatchFacade> all= new ArrayList<>();
        //CockID, Winner, Looser
        HashMap<Integer,int[]> BattleReport = new HashMap<>();
//        int[][] jagged = new int[x][];
        for(int i = 0;i<x;i++){
//            int[] num = new int[x-i-1];
            for(int j =i+1;j<x;j++){
                MatchFacade mf = new MatchFacade(all.size(), nIds.get(i),nIds.get(j));
                //Cock c1 = allC.get(nIds.get(i)).clone();
                //Cock c2 = allC.get(nIds.get(j)).clone();
                //int winner = Helpers.Fight(c1,c2);
                //int loser = winner == c1.getCockID() ? c2.getCockID(): c1.getCockID();
//                num[j-i-1]= winner;
                //Helpers.addIfNotExist(BattleReport,winner,0,1);
                //Helpers.addIfNotExist(BattleReport,loser,1,1);
//                System.out.print(j+" ");
                all.add(mf);
            }
//            jagged[i] = num;
//            System.out.println();
        }
        MotherThreadController mfc = new MotherThreadController(all,5);
        Thread MotherThread = new Thread(mfc);
        MotherThread.start();
        while(MotherThread.isAlive()){
            System.out.println("Mother is Looping");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        HashMap<Integer,Integer> results =  mfc.getThreadResults();
        for(Integer MatchID : results.keySet()){
            //
            MatchFacade currmf = all.get(MatchID);
            int[] milfs = currmf.getCcks();
            int winner = results.get(MatchID);
            int loser  = winner == milfs[0] ? milfs[1] : milfs[0];
            Helpers.addIfNotExist(BattleReport,winner,0,1);
            Helpers.addIfNotExist(BattleReport,loser,1,1);
        }
//        dbh.batchSetWinner(MTC.getThreadResults());
//        System.out.println("Congratulations!!!");
        return BattleReport;
    }
}
