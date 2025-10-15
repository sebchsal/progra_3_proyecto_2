package org.example.sistemarecetas.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Medico extends  Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1); // ID por clase Medico
    private final int id;
    private String especialidad;

    public Medico(String identificacion, String nombre, String clave, String especialidad) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
        this.especialidad = especialidad;
    }

    @Override public int getId() { return id; }

    // 🔹 Constructor especial para XML (respeta el ID cargado)
    public Medico(int id, String identificacion, String nombre, String clave, String especialidad) {
        super(identificacion, nombre, clave);
        this.id = id;
        this.especialidad = especialidad;

        // sincroniza la secuencia con el ID leído
        SEQ.updateAndGet(cur -> Math.max(cur, id + 1));
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public static void resetSequenceTo(int nextValue) {
        SEQ.set(nextValue);
    }
}
