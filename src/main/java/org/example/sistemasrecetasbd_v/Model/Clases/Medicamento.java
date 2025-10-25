package org.example.sistemasrecetasbd_v.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Medicamento {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private int id;
    private String codigo;
    private String nombre;
    private String tipoPresentacion;

    public Medicamento() {}

    public Medicamento(String codigo, String nombre, String tipoPresentacion) {
        this.id = SEQ.getAndIncrement();
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoPresentacion = tipoPresentacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre;}
    public void setNombre(String nombre) { this.nombre = nombre;}

    public String getTipoPresentacion() { return tipoPresentacion;}
    public void setTipoPresentacion(String tipoPresentacion) { this.tipoPresentacion = tipoPresentacion;}

    public String getCodigo() { return codigo;}
    public void setCodigo(String codigo) { this.codigo = codigo;}

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
