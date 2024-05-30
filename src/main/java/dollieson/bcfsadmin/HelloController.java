package dollieson.bcfsadmin;

import dollieson.bcfsadmin.BackEnd.Attacks.AttackModule;
import dollieson.bcfsadmin.BackEnd.Builders.AttackModuleBuilder;
import dollieson.bcfsadmin.BackEnd.DB.AttackHelper;
import dollieson.bcfsadmin.BackEnd.Globals.DBHelpers;
import dollieson.bcfsadmin.BackEnd.Globals.Helpers;
import dollieson.bcfsadmin.BackEnd.Main.Attack;
import dollieson.bcfsadmin.BackEnd.Main.Cock;
import dollieson.bcfsadmin.BackEnd.Main.MatchFacade;
import dollieson.bcfsadmin.BackEnd.Threading.MotherThreadController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
    public ScrollPane spUnverifiedTable;
    public Label lblUnverCount;
    public Button btnRefeshUnverified;
    public Button btnVerifyBtls;
    public ScrollPane spAllversusAll;

    private int contcol = 5;


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
        repopulateAttacktbl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choices = new String[]{"Single Attack","Leech","Heal"};
        cbAttackMod.setItems(FXCollections.observableArrayList(choices));
        cbAttackMod.getSelectionModel().select(0);
        repopulateAttacktbl();;
        repopulateUnverifiedTable();
    }
    public AttackModule getAttackMod(String ch){
        for(int x = 0;x<choices.length;x++){
            if(choices[x].equals(ch)){
                return AttackModuleBuilder.buildAttackModule(x);
            }
        }
        return null;
    }
    public void repopulateAttacktbl(){
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
                ac.setToggle(atk.getAttackID(),atk.getIsDisabled());
                ac.setParent(this);
                gp.add(sc,num_atk%contcol,num_atk/contcol);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            num_atk++;
        }
        spContainer.setContent(gp);
    }
    public void repopulateUnverifiedTable(){
        System.out.println("Getting Unverified Tables......");
        GridPane gp =  new GridPane();
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        HashMap<Integer, Cock> allC = dbh.getAllCockData();
        System.out.println("All C Done");
        HashMap<Integer,String> OwnerNames  = dbh.getAllDIsplayNames();
        System.out.println("OwnerNames Done");
        ArrayList<MatchFacade> MF = dbh.getAllUnverifiedMatches();
        lblUnverCount.setText(String.format("%d Unverified Battles",MF.size()));
        System.out.println("Unverified Done");
        for(int y =0;y<MF.size();y++){
            MatchFacade mf = MF.get(y);
            int[] cocks = mf.getCcks();
            Label fightNum = new Label(String.format("Fight ID. %d",mf.getMatchID()));
            gp.add(fightNum,0,y);
            try {
                for(int x =0;x<cocks.length;x++){
                    int cockID = cocks[x];
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("unverifiedFights.fxml"));
                    Parent sc = fxmlLoader.load();
                    unverifiedFightsController ufc = fxmlLoader.getController();
                    ufc.setLabels(allC.get(cockID),OwnerNames);
                    gp.add(sc,x+1,y);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        spUnverifiedTable.setContent(gp);
    }


    public void verifyAllMatch(){
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());

//        try(Connection C = DBHelpers.getGlobalConnection().getConnection()){
//            Statement st = C.createStatement();
//            st.execute("UPDATE tblmatch SET winner = 0 WHERE 1");
//            Thread.sleep(1000);
//        } catch (SQLException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        ArrayList<MatchFacade> Matches = dbh.getAllUnverifiedMatches();
        System.out.println("Done Getting Fights");
        HashMap<Integer,Cock> allcocks = dbh.getAllCockData();
        MotherThreadController MTC = new MotherThreadController(Matches,5);
        Thread MotherThread = new Thread(MTC);
        MotherThread.start();
        while(MotherThread.isAlive()){
            System.out.println("Mother is Looping");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        dbh.batchSetWinner(MTC.getThreadResults());
        repopulateUnverifiedTable();
    }
    public void activateAllVersusAll(){
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        HashMap<Integer,Cock> allC = dbh.getAllCockData();
        HashMap<Integer,String> allUser = dbh.getAllDIsplayNames();
        HashMap<Integer,int[]> MatchResult = Helpers.allVersusAll(allC);
        GridPane gp = new GridPane();
        ArrayList<FightResultContainer> frc = new ArrayList<>();

        for(int cockID : MatchResult.keySet()){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AvAres.fxml"));
                Parent resultParent = fxmlLoader.load();
                AvAres cont = fxmlLoader.getController();
                Cock cock = allC.get(cockID);
                String CockName = cock.getName();
                String Owner = allUser.get(cock.getOwnerID());
                int[] winloss = MatchResult.get(cockID);
                cont.setLabels(CockName,Integer.toString(cockID),Owner,winloss);
                FightResultContainer fightResultContainer = new FightResultContainer(resultParent,winloss,cockID);
                frc.add(fightResultContainer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Collections.sort(frc,new FightResultContainer.Sort());
        for(int x = 0 ;x<frc.size();x++){
            FightResultContainer fightResultContainer = frc.get(x);
            gp.add(fightResultContainer.getPrintable(),x%3,x/3);
        }
        spAllversusAll.setContent(gp);
    }

}