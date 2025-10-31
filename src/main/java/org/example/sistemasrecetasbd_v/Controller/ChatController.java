package org.example.sistemasrecetasbd_v.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.example.sistemasrecetasbd_v.Servicios.UserChat;

public class ChatController {
    @FXML private TextArea txtAreaMensajesChat;
    @FXML private TextField txtEscribirMensajeChat, txtNombreCompletoChat;
    @FXML private Button btnConectarChat;

    private UserChat usuario;

    @FXML
    public void onConnect() {
        try {
            usuario = new UserChat();
            usuario.connect("localhost", 6000, txtNombreCompletoChat.getText(),
                    msg -> Platform.runLater(() -> txtAreaMensajesChat.appendText(msg + "\n")));
            btnConectarChat.setDisable(true);
        } catch (Exception e) {
            txtAreaMensajesChat.appendText("Error al conectar: " + e.getMessage() + "\n");
        }
    }

    @FXML
    public void onSend() {
        String msg = txtEscribirMensajeChat.getText().trim();
        if (msg.isEmpty()) return;
        usuario.sendMessage(msg);
        txtEscribirMensajeChat.clear();
    }
}
