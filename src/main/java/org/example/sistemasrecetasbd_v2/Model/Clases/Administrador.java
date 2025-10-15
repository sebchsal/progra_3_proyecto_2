package org.example.sistemarecetas.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Administrador extends Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final int id;

    public Administrador(String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
    }

    public Administrador(int id, String nombreCompleto, String identificacion, String clave) {
        super(identificacion, nombreCompleto, clave);
        this.id = id;
    }

   @Override public int getId() { return id; }

    public static void resetSequenceTo(int nextValue) {
        SEQ.set(nextValue);
    }
}
