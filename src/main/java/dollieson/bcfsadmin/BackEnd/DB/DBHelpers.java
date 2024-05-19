package dollieson.bcfsadmin.BackEnd.DB;

import dollieson.bcfsadmin.BackEnd.Attacks.AttackHelper;
import dollieson.bcfsadmin.BackEnd.Attacks.AttackModule;
import dollieson.bcfsadmin.BackEnd.Builders.AttackModuleBuilder;
import dollieson.bcfsadmin.BackEnd.Globals.Attack;
import dollieson.bcfsadmin.BackEnd.Globals.Cock;
import dollieson.bcfsadmin.BackEnd.Globals.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelpers {

    /**is an interface to allow modularity in connections**/
    private static DBConnection dbConnection;
    /**to allow a unified connection when constructing DBHelpers <br> DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection()) <br> So that we can easily change from supabase to localhost for testing<**/
    private static DBConnection globalConnection;
    public DBHelpers(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public static DBConnection getGlobalConnection(){
        return globalConnection;
    }

    public static void setGlobalConnection(DBConnection dbc){
        globalConnection = dbc;
    }

    public HashMap<Integer, Attack> getAllAttacks() {
        HashMap<Integer, Attack> allAttacks = new HashMap<>();
        try (Connection C = dbConnection.getConnection();
             Statement statement = C.createStatement()) {
            String querry = "SELECT * FROM tblattack";
            ResultSet rs = statement.executeQuery(querry);
            while (rs.next()) {
                String aName = rs.getString("name");
                int aSpeed = rs.getInt("speed");
                int aDamage = rs.getInt("damage");
                double adamageMult = rs.getDouble("damageMultiplier");
                int attackModule = rs.getInt("attackModule");
                int Id = rs.getInt("attackID");
                AttackModule AM = AttackModuleBuilder.buildAttackModule(attackModule);
                Attack atk = new Attack(aName, aSpeed, aDamage, adamageMult, AM,Id);
                allAttacks.put(Id, atk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allAttacks;
    }

    public static User LoginUser(String Username, String Password) {
        try (Connection c = dbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT UserID,DisplayName FROM tbluser WHERE Username = ? AND Password = ?")) {
            ps.setString(1, Username);
            ps.setString(2, Password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int UID = rs.getInt("UserID");
            String DisplayName = rs.getString("DisplayName");
            User.setCurrentUser(DisplayName,UID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return User.getCurrUser();
    }

    public static boolean SendCockData(Cock cock) {
        ArrayList<Attack> lists = cock.getAttackList();
        boolean result = false;
        try (Connection c = dbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO `tblcock` (`UserID`, `CockName`, `Attack1ID`, `Attack2ID`, `Attack3ID`, `Attack4ID`) VALUES (?, ?, ? , ? , ? , ?)")) {
            int startInd = 3;
            ps.setInt(1, cock.getOwnerID());
            ps.setString(2, cock.getName());
            for (Attack atk : lists) {
                int atkID = AttackHelper.attackModuleToInt(atk.getAttackModule());
                ps.setInt(startInd++, atkID);
            }
            while (startInd-2 <= Cock.MAX_ATTACKS) {
                ps.setInt(startInd++, 0);
            }
            System.out.println(ps.toString());
            ps.execute();
            System.out.println("Cock Insert Successfull");
            // fetch cockdata
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int getCockID(Cock cock){
            ArrayList<Attack> lists = cock.getAttackList();
            int res = 0;
            try(Connection C = dbConnection.getConnection();
            PreparedStatement ps = C.prepareStatement("SELECT CockID FROM tblcock WHERE UserID = ? AND CockName = ? AND Attack1ID = ? AND Attack2ID = ? AND Attack3ID = ? AND Attack4ID = ?")) {
                ps.setInt(1,cock.getOwnerID());
                ps.setString(2,cock.getName());
                int startInd = 3;
                for(Attack atk: lists){
                    ps.setInt(startInd++,AttackHelper.attackModuleToInt(atk.getAttackModule()));
                }
                while(startInd-2 <= Cock.MAX_ATTACKS){
                    ps.setInt(startInd++,0);
                }
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    cock.setCockID(rs.getInt("CockID"));
                }
                res = cock.getCockID();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return res;
    }

    public static HashMap<Integer, Cock> getAllCockData(){
        HashMap<Integer, Cock> cockData = null;
        try(Connection c = dbConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM tblcock")){
            ResultSet rs = ps.executeQuery();
            cockData = new HashMap<>();
            while(rs.next()){
                Cock cock = new Cock(
                        rs.getString("CockName"),
                        rs.getInt("UserID")
                );
                int[] AttackIDs = {rs.getInt("Attack1ID"),rs.getInt("Attack2ID"),rs.getInt("Attack3ID"),rs.getInt("Attack4ID")};
                HashMap<Integer,Attack> allAttack = AttackHelper.fetchAllAttack();
                for(int AIDs: AttackIDs){
                    if(AIDs == 0) break;
                    Attack tempAtk = AttackHelper.cloneAttack(allAttack.get(AIDs));
                    cock.addAttack(tempAtk);
                }
                cockData.put(rs.getInt("CockID"),cock);
            }
            System.out.println("Cocks Fetched Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cockData;
    }

    //TODO: Update, Using The Outdated tbl
    public static boolean challengePlayer(int invitorCockID,int inviteeID, int invitorID){
        boolean isSuccess = false;
        try(Connection C = dbConnection.getConnection();){
            PreparedStatement ps = C.prepareStatement("Insert into tblinvite(invitorCockID,inviteeID,invitorID) values(?,?,?)");
            ps.setInt(1,invitorCockID);
            ps.setInt(2,inviteeID);
            ps.setInt(3,invitorID);
            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean acceptInvite(int inviteID, int inviteeCockID){
        try(Connection C = dbConnection.getConnection();){
            PreparedStatement ps = C.prepareStatement("UPDATE tblinvite set isAccepted = 1 where InviteID = ?");
            ps.setInt(1,inviteID);
            boolean isSuccess =  ps.execute();
            if(isSuccess){
                //createMatch
                PreparedStatement ps1 = C.prepareStatement("Select invitorCockID from tblinvite where InviteID = ?");
                ps1.setInt(1,inviteID);
                ResultSet rs = ps1.executeQuery();
                int invitorCockID = -1;
                while(rs.next()){
                    invitorCockID = rs.getInt("invitorCockID");
                    break;
                }

                if(invitorCockID==-1){
                    System.out.println("An Error occured while getting the invitorCockID");
                    return false;
                }else{
                    createMatch(invitorCockID,inviteeCockID);
                    return true;
                }

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }




    public static boolean createMatch(int invitorCockID, int inviteeCockID){

        try(Connection C = dbConnection.getConnection();){
            PreparedStatement ps = C.prepareStatement("Insert into tblmatch(invitorCockID,inviteeCockID) values (?,?)");
            ps.setInt(1,invitorCockID);
            ps.setInt(2,inviteeCockID);
            return  ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static ArrayList<Integer> getChallenges(int userID){
        ArrayList<Integer> inviteIds = new ArrayList<>();

        try(Connection C = dbConnection.getConnection();
            PreparedStatement ps = C.prepareStatement("Select InviteID from tblinvite where isChallenge = 1 and referenceID = ?")){
            ps.setInt(1,userID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                inviteIds.add(rs.getInt("InviteID"));
            }
            return inviteIds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static boolean updateDetails(int userID, String displayName, String username, String Password){
        try(Connection C = dbConnection.getConnection();
            PreparedStatement ps = C.prepareStatement("UPDATE tbluser Set DisplayName = ?, Username = ?, Password = ? where UserID = ?")){
            ps.setString(1,displayName);
            ps.setString(2,username);
            ps.setString(3,Password);
            ps.setInt(4,userID);
            return ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getDisplayName(int userid){
//        returns displayname given userid
        //returns null if user id doesn't exists

        try(Connection C = dbConnection.getConnection();){
            PreparedStatement ps = C.prepareStatement("Select DisplayName from tbluser where UserID = ?");
            ps.setInt(1,userid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString("DisplayName");
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }



        public static int getInvitorCockID(int inviteID){
            try (Connection c = dbConnection.getConnection();) {
                PreparedStatement ps = c.prepareStatement("Select CockID from tblinvite where InviteID = ?");
                ps.setInt(  1, inviteID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    return rs.getInt("CockID");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return -1; //returns -1 if error occurs
        }

        public static boolean insertMatch(int invitorCockID, int inviteeCockId){
            try (Connection c = dbConnection.getConnection();) {
                PreparedStatement ps = c.prepareStatement("Insert into tblmatch(invitorCockID, inviteeCockID) values (?,?)");
                ps.setInt(  1, invitorCockID);
                ps.setInt(  1, inviteeCockId);
                return ps.execute();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean valueExists(String tablename, String columnname, String value){
            try (Connection c = dbConnection.getConnection();) {
                PreparedStatement ps = c.prepareStatement("Select * from ? where ? = ?");
                ps.setString(1,tablename);
                ps.setString(2, columnname);
                ps.setString(3,value);
                ResultSet rs = ps.executeQuery();
                int number_of_rows = rs.getRow();
                return number_of_rows!=0;


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static int createAccount(String DisplayName,String Username, String Password){
            try (Connection c = dbConnection.getConnection();) {
                boolean usernameExists = valueExists("tbluser","Username",Username);
                if(usernameExists ){
                    System.out.println("Username already exists");
                    return -1;
                }else{
                    PreparedStatement ps = c.prepareStatement("Insert into tbluser(Displayname,Username,Password) values (?,?,?)");
                    ps.setString(1,DisplayName);
                    ps.setString(2,Username);
                    ps.setString(3,Password);
                    ps.execute();
                    return getUserId(Username);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static int getUserId(String username){
            try (Connection c = dbConnection.getConnection();) {
                PreparedStatement ps = c.prepareStatement("Select UserID from tbluser where Username=?");
                ps.setString(1,username);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    return rs.getInt("UserID");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return -1;
        }
        public boolean sendAttack(Attack atk){
            boolean res = false;
            try(Connection c = dbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO tblattack(name,speed,damage,damageMultiplier,attackModule) values (?,?,?,?,?)")){
                ps.setString(1,atk.getName());
                ps.setInt(2,atk.getSpeed());
                ps.setInt(3,atk.getDamage());
                ps.setDouble(4,atk.getDamageMultiplier());
                ps.setInt(5,AttackHelper.attackModuleToInt(atk.getAttackModule()));
                res = ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return res;
        }
}
