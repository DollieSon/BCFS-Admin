package dollieson.bcfsadmin;

import dollieson.bcfsadmin.BackEnd.Main.Attack;
import dollieson.bcfsadmin.BackEnd.Main.Cock;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.HashMap;

public class unverifiedFightsController {

    @FXML
    private Label lblAtkDmg1;

    @FXML
    private Label lblAtkDmg2;

    @FXML
    private Label lblAtkDmg3;

    @FXML
    private Label lblAtkDmg4;

    @FXML
    private Label lblAtkSpeed1;

    @FXML
    private Label lblAtkSpeed2;

    @FXML
    private Label lblAtkSpeed3;

    @FXML
    private Label lblAtkSpeed4;

    @FXML
    private Label lblCockID;

    @FXML
    private Label lblCockName;

    @FXML
    private Label lblOwnerName;

    @FXML
    private Label lblatkName1;

    @FXML
    private Label lblatkName2;

    @FXML
    private Label lblatkName3;

    @FXML
    private Label lblatkName4;

    public void setLabels(Cock cock, HashMap<Integer,String> ownerNames){
        lblCockID.setText(Integer.toString(cock.getCockID()));
        lblCockName.setText(cock.getName());
        lblOwnerName.setText(ownerNames.get(cock.getOwnerID()));
        Label[] atkNames = {lblatkName1,lblatkName2,lblatkName3,lblatkName4};
        Label[] atkDamage= {lblAtkDmg1,lblAtkDmg2,lblAtkDmg3,lblAtkDmg4};
        Label[] atkSpeed= {lblAtkSpeed1,lblAtkSpeed2,lblAtkSpeed3,lblAtkSpeed4};
        ArrayList<Attack> atkList = cock.getAttackList();
        for(int x =0;x<4;x++){
            String Name = "";
            String Damage = "";
            String Speed = "";
           if(x < atkList.size()){
               Attack atk = atkList.get(x);
               Name = atk.getName();
               Damage = Integer.toString(atk.getDamage());
               Speed = Integer.toString(atk.getSpeed());
           }
           atkNames[x].setText(Name);
           atkDamage[x].setText(Damage);
           atkSpeed[x].setText(Speed);
        }
    }

}
