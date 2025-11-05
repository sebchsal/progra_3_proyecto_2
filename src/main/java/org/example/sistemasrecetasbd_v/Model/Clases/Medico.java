package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Medico extends  Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1); // ID por clase Medico
    private int idM;
    private String especialidad;

    public Medico(){}

    public Medico(String identificacion, String nombre, String clave, String especialidad) {
        super(identificacion, nombre, clave);
        this.idM = SEQ.getAndIncrement();
        this.especialidad = especialidad;
    }

    @Override public int getId() { return idM; }
    @Override public void setId(int id) { this.idM = id; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
