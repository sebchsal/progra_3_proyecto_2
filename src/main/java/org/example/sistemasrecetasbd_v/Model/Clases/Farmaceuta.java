package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Farmaceuta extends Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private int id;

    public Farmaceuta() {}

    public Farmaceuta(String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
