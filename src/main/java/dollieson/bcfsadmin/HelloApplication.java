package dollieson.bcfsadmin;

import dollieson.bcfsadmin.BackEnd.DB.LocalHostConnection;
import dollieson.bcfsadmin.BackEnd.DB.MySQLConnection;
import dollieson.bcfsadmin.BackEnd.Globals.DBHelpers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DBHelpers.setGlobalConnection(new LocalHostConnection());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}