package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Farmaceuta extends Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final int id;

    public Farmaceuta(String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
    }

    public Farmaceuta(int id, String identificacion, String nombre, String clave) {
        super(identificacion, nombre, clave);
        this.id = id;
        SEQ.updateAndGet(cur -> Math.max(cur, id + 1));
    }

    @Override public int getId() { return id; }

    public static void resetSequenceTo(int nextValue) {
        SEQ.set(nextValue);
    }
}
