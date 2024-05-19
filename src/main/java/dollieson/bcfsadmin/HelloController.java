package dollieson.bcfsadmin;

import dollieson.bcfsadmin.BackEnd.Attacks.AttackHelper;
import dollieson.bcfsadmin.BackEnd.Attacks.AttackModule;
import dollieson.bcfsadmin.BackEnd.Builders.AttackModuleBuilder;
import dollieson.bcfsadmin.BackEnd.DB.DBHelpers;
import dollieson.bcfsadmin.BackEnd.Globals.Attack;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public TextField tfName;
    public TextField tfSpeed;
    public TextField tfDamage;
    public TextField tfDamageMult;
    public ChoiceBox cbAttackMod;
    public Button btnSave;
    public String choices[];
    public ScrollPane spContainer;

    private int contcol = 4;


    @FXML
    protected void onSaveClick() {
        String Name = tfName.getText();
        int speed = Integer.parseInt(tfSpeed.getText());
        int damage= Integer.parseInt(tfDamage.getText());;
        double damageMult= Double.parseDouble(tfDamageMult.getText());
        AttackModule AtkMod =AttackModuleBuilder.buildAttackModule(cbAttackMod.getSelectionModel().getSelectedIndex()+1);
        System.out.println(String.format("%s %d %d %f", Name,speed,damage,damageMult));
        Attack atk = new Attack(Name,speed,damage,damageMult,AtkMod);
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        dbh.sendAttack(atk);
        RepopulateTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choices = new String[]{"Single Attack","Leech","Heal"};
        cbAttackMod.setItems(FXCollections.observableArrayList(choices));
        cbAttackMod.getSelectionModel().select(0);
        RepopulateTable();;
    }
    public AttackModule getAttackMod(String ch){
        for(int x = 0;x<choices.length;x++){
            if(choices[x].equals(ch)){
                return AttackModuleBuilder.buildAttackModule(x);
            }
        }
        return null;
    }
    public void RepopulateTable(){
        System.out.println("repopulating.....");
        GridPane gp = new GridPane();
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        HashMap<Integer,Attack> allattacks = dbh.getAllAttacks();
        int num_atk = 0;
        for(Attack atk: allattacks.values()){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("attackContainer.fxml"));
            try {
                Parent sc =fxmlLoader.load();
                AttackContainer ac = fxmlLoader.getController();
                int type = AttackHelper.attackModuleToInt(atk.getAttackModule());
                ac.Setlabels(atk.getName(),String.valueOf(atk.getDamage()),String.valueOf(atk.getSpeed()),choices[type-1]);
                gp.add(sc,num_atk%contcol,num_atk/contcol);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            num_atk++;
        }
        spContainer.setContent(gp);
    }
}