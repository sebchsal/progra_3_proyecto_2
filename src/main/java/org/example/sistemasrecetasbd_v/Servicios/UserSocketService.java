package org.example.sistemasrecetasbd_v.Servicios;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class UserSocketService {
    private final String host = "localhost";
    private final int port = 5000;

    public String enviar(String json) throws Exception {
        try (Socket s = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis  = new DataInputStream(s.getInputStream())) {

            dos.writeUTF(json);
            dos.flush();

            return dis.readUTF();
        }
    }
}
