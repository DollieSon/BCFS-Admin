package dollieson.bcfsadmin;

import dollieson.bcfsadmin.BackEnd.Globals.DBHelpers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class AttackContainer {
    public Label lblName;
    public Label lblPower;
    public Label lblSpeed;
    public Label lblType;
    public Button btnToggle;
    public AnchorPane apBackground;
    private int AttackID;
    private boolean isDisabled;
    private HelloController parent;

    public void Setlabels(String Name,String Power,String Speed,String Type){
        lblName.setText(Name);
        lblPower.setText(Power);
        lblSpeed.setText(Speed);
        lblType.setText(Type);
    }
    public  void setToggle(int atkID, boolean state){
        AttackID = atkID;
        isDisabled = state;
        Color bg = Color.GREEN;
        if(isDisabled){
            btnToggle.setText("Enable");
            bg = Color.RED;

        }else{
            btnToggle.setText("Disable");
        }
        apBackground.setBackground(new Background(new BackgroundFill(bg,null,null)));
    }
    public void setParent(HelloController p){
        parent = p;
    }
    @FXML
    public void toggleAttack(){
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        dbh.toggleIsDisabled(AttackID,!isDisabled);
        parent.repopulateAttacktbl();
    }
}
