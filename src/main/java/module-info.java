module org.example.sistemasrecetasbd_v {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;

    opens org.example.sistemasrecetasbd_v to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Controller;
    opens org.example.sistemasrecetasbd_v.Controller to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Principal;
    opens org.example.sistemasrecetasbd_v.Principal to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Data;
}