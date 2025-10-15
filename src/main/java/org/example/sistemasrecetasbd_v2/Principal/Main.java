package org.example.sistemarecetas.Principal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/example/sistemarecetas/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 512, 375);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
