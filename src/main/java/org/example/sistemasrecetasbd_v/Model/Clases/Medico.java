package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Medico extends  Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1); // ID por clase Medico
    private int id;
    private String especialidad;

    public Medico(){}

    public Medico(String identificacion, String nombre, String clave, String especialidad) {
        super(identificacion, nombre, clave);
        this.id = SEQ.getAndIncrement();
        this.especialidad = especialidad;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
