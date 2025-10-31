package org.example.sistemasrecetasbd_v.Servicios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

// Acepta conexiones de usuarios (médicos, farmaceutas, administradores)

public class SisChatServer {
    private final int port;
    private final Set<UserHandler> users = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(SisChatServer.class.getName());

    public SisChatServer(int port) {
        this.port = port;
        configureLogger();
    }

    private void configureLogger() {
        try {
            LOGGER.setUseParentHandlers(false);
            var fileHandler = new FileHandler("sis_server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("No se pudo generar la bitácora del servidor de chat.");
        }
    }

    public void start() {
        LOGGER.info("Servidor del sistema de recetas iniciado en el puerto: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                UserHandler handler = new UserHandler(socket, this);
                users.add(handler);
                handler.start();
                LOGGER.info("Nueva conexión desde: " + socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en el servidor: " + e.getMessage(), e);
        }
    }

    public void broadcast(String message) {
        synchronized (users) {
            for (UserHandler u : users) u.send(message);
        }
    }

    protected void remove(UserHandler handler) {
        users.remove(handler);
        LOGGER.info("Usuario desconectado: " + handler.getName());
    }

    public static void main(String[] args) {
        new SisChatServer(6000).start();
    }
}
