package dollieson.bcfsadmin.BackEnd.Threading;


import dollieson.bcfsadmin.BackEnd.Globals.DBHelpers;
import dollieson.bcfsadmin.BackEnd.Main.Cock;
import dollieson.bcfsadmin.BackEnd.Main.MatchFacade;

import java.util.ArrayList;
import java.util.HashMap;

public class MotherThreadController implements Runnable{
    private int num_threads;
    private ArrayList<MatchThread> ChildThreads;
    private HashMap<Integer,Integer> ThreadResults;
    private boolean isFinished;

    public MotherThreadController(ArrayList<MatchFacade> mfs, int threads){
        isFinished = false;
        num_threads = threads;
        DBHelpers dbh = new DBHelpers(DBHelpers.getGlobalConnection());
        ChildThreads = new ArrayList<>();
        ThreadResults = new HashMap<>();
        HashMap<Integer, Cock> allcock = dbh.getAllCockData();
        for(int x=0;x<threads;x++){
            ChildThreads.add(new MatchThread(ThreadResults,allcock));
        }
        for(int x=0;x<mfs.size();x++){
            MatchFacade mf = mfs.get(x);
            //System.out.println("Inserting Match: " + mf.getMatchID() );
            ChildThreads.get(x%threads).addTask(mf);
        }
    }

    public HashMap<Integer, Integer> getThreadResults() {
        return ThreadResults;
    }

    public boolean isFinished() {
        return isFinished;
    }

    private void startMatches(){
        ArrayList<Thread> runs = new ArrayList<>();
        boolean isempty = true;
        for(MatchThread mt: ChildThreads){
            Thread child = new Thread(mt);
            runs.add(child);
            child.start();
        }
        for(MatchThread mt:ChildThreads){
            if(!mt.isempty()) isempty = false;
        }
        if(isempty) return;
        do{
            isFinished = true;
            for(Thread run : runs){
                if(run.isAlive()){
                    isFinished = false;
                    break;
                }
            }
            for(int x=0;x<ChildThreads.size();x++){
                MatchThread child = ChildThreads.get(x);
                System.out.println(x+" Status: "+child.getCurrentItem());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }while (!isFinished);
    }

    @Override
    public void run() {
        startMatches();
    }
}
