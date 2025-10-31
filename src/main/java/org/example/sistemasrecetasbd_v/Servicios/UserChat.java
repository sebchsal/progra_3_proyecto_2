package org.example.sistemasrecetasbd_v.Servicios;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

// Cliente del chat (puede representar a un Medico, Farmaceuta o Administrador)
public class UserChat {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Se conecta al servidor e inicia el hilo de escucha
    public void connect(String host, int port, String nombre, Consumer<String> onMsg) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Escucha mensajes del servidor
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) onMsg.accept(line);
            } catch (IOException e) {
                onMsg.accept("[Sistema] Desconectado del servidor.");
            }
        }).start();

        // Enviar nombre de usuario al conectar
        out.println(nombre);
    }

    // Envía un mensaje al chat
    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }

    // Cierra la conexión
    public void close() throws IOException {
        if (socket != null) socket.close();
    }
}
