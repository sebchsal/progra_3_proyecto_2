package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Usuario {
    private static final AtomicInteger SEQ = new AtomicInteger(1); // secuencia por clase base
    private final int id;                 // inmutable tras crear
    private String nombre;
    private String clave;
    private String identificacion;

    protected Usuario(String identificacion,String nombre, String clave) {
        this.id = SEQ.getAndIncrement();
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.clave = clave;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
}