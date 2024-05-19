module dollieson.bcfsadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens dollieson.bcfsadmin to javafx.fxml;
    exports dollieson.bcfsadmin;
}