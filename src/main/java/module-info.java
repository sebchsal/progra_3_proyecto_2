module org.example.sistemasrecetasbd_v2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.sistemasrecetasbd_v2 to javafx.fxml;
    exports org.example.sistemasrecetasbd_v2;
}