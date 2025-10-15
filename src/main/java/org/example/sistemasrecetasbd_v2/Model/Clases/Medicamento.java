package org.example.sistemarecetas.Model.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Medicamento {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final int id;
    private String codigo;
    private String nombre;
    private String tipoPresentacion;

    public Medicamento(String codigo, String nombre, String tipoPresentacion) {
        this.id = SEQ.getAndIncrement();
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoPresentacion = tipoPresentacion;
    }

    // Constructor especial para XML
    public Medicamento(int id, String codigo, String nombre, String tipoPresentacion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoPresentacion = tipoPresentacion;

        // sincroniza la secuencia con el ID leído
        SEQ.updateAndGet(cur -> Math.max(cur, id + 1));
    }

    public int getId() { return id; }

    public String getNombre() { return nombre;}
    public void setNombre(String nombre) { this.nombre = nombre;}

    public String getTipoPresentacion() { return tipoPresentacion;}
    public void setTipoPresentacion(String tipoPresentacion) { this.tipoPresentacion = tipoPresentacion;}

    public String getCodigo() { return codigo;}
    public void setCodigo(String codigo) { this.codigo = codigo;}

    public static void resetSequenceTo(int nextValue) {
        SEQ.set(nextValue);
    }
}
