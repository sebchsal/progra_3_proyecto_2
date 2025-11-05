package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Farmaceuta extends Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private int idF;

    public Farmaceuta() {}

    public Farmaceuta(String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.idF = SEQ.getAndIncrement();
    }

    @Override public int getId() { return idF; }
    @Override public void setId(int id) { this.idF = id; }

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
