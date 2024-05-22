package dollieson.bcfsadmin;

import javafx.scene.Parent;

import java.util.Comparator;

public class FightResultContainer {
    private Parent printable;
    private int[] winloss;
    private int cockID;

    public FightResultContainer(Parent printable, int[] winloss, int cockID) {
        this.printable = printable;
        this.winloss = winloss;
        this.cockID = cockID;
    }
    public static class Sort implements Comparator<FightResultContainer>{
        @Override
        public int compare(FightResultContainer o1, FightResultContainer o2) {
            int res = Integer.compare(o1.winloss[0],o2.winloss[0]);
            if(res == 0 ){
                res = Integer.compare(o2.cockID,o1.cockID);
            }
            return res;
        }
    }
    public Parent getPrintable(){
        return printable;
    }
}
