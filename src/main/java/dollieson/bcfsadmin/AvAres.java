package dollieson.bcfsadmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class AvAres {

    public Label lblWins;
    public Label lblLoses;
    @FXML
    private Label lblCockID;

    @FXML
    private Label lblCockName;

    @FXML
    private Label lblCockOwner;

    @FXML
    private PieChart pcWinrate;

    public void setLabels(String CName,String CID ,String Owner, int[] winrate){
        lblLoses.setText(Integer.toString(winrate[1]));
        lblWins.setText(Integer.toString(winrate[0]));
        lblCockID.setText(CID);
        lblCockName.setText(CName);
        lblCockOwner.setText(Owner);
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList(
                new PieChart.Data("Wins",winrate[0]),
                new PieChart.Data("Losses",winrate[1])
        );
        pcWinrate.setData(pd);
    }
}
