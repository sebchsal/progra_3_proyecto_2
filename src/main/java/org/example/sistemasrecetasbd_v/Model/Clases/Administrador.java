package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Administrador extends Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final int id;

    public Administrador(String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
    }

   @Override public int getId() { return id; }

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
