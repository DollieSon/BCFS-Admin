package dollieson.bcfsadmin;

import javafx.scene.control.Label;

public class AttackContainer {
    public Label lblName;
    public Label lblPower;
    public Label lblSpeed;
    public Label lblType;

    public void Setlabels(String Name,String Power,String Speed,String Type){
        lblName.setText(Name);
        lblPower.setText(Power);
        lblSpeed.setText(Speed);
        lblType.setText(Type);
    }
}
