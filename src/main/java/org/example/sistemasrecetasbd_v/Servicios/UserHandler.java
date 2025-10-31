package org.example.sistemasrecetasbd_v.Servicios;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// Gestiona la conexión de un usuario individual dentro del sistema de chat.
//Cada instancia corre en su propio hilo.
public class UserHandler extends Thread {
    private final Socket socket;
    private final SisChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String userName = "Invitado";

    private static final Logger LOGGER = Logger.getLogger("SisChatServer");

    public UserHandler(Socket socket, SisChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Bienvenido al chat del Sistema de Recetas. Envíe su nombre como primer mensaje.");

            String line = in.readLine();
            if (line != null && !line.isBlank()) {
                userName = line.trim();
                server.broadcast("[Sistema] " + userName + " se ha unido al chat.");
            }

            while ((line = in.readLine()) != null) {
                server.broadcast(userName + ": " + line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Conexión finalizada: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            server.remove(this);
        }
    }
}
