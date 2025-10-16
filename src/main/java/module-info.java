module org.example.sistemasrecetasbd_v {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jakarta.xml.bind;

    opens org.example.sistemasrecetasbd_v to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Controller;
    opens org.example.sistemasrecetasbd_v.Controller to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Principal;
    opens org.example.sistemasrecetasbd_v.Principal to javafx.fxml;

    exports org.example.sistemasrecetasbd_v.Persistencia;
    opens org.example.sistemasrecetasbd_v.Persistencia to jakarta.xml.bind;
    exports org.example.sistemasrecetasbd_v.Persistencia.Conector;
    opens org.example.sistemasrecetasbd_v.Persistencia.Conector to jakarta.xml.bind;
    exports org.example.sistemasrecetasbd_v.Persistencia.Datos;
    opens org.example.sistemasrecetasbd_v.Persistencia.Datos to jakarta.xml.bind;
    exports org.example.sistemasrecetasbd_v.Persistencia.Entity;
    opens org.example.sistemasrecetasbd_v.Persistencia.Entity to jakarta.xml.bind;
    exports org.example.sistemasrecetasbd_v.Model.Clases;
    opens org.example.sistemasrecetasbd_v.Model.Clases to javafx.fxml;
}