package org.example.sistemasrecetasbd_v.Model.Clases;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class Receta {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private int id;
    private String estado;
    private int cantidad;
    private String detalle;
    private Medicamento medicamento;
    private Paciente paciente;
    private LocalDate fechaConfeccion;
    private LocalDate fechaEntrega;

    public Receta() {}

    public Receta(String estado, int cantidad, String detalle, Medicamento medicamento, Paciente paciente, LocalDate fechaC, LocalDate fechaE) {
        this.id = SEQ.getAndIncrement();
        this.estado = estado;
        this.cantidad = cantidad;
        this.detalle = detalle;
        this.medicamento = medicamento;
        this.paciente = paciente;
        this.fechaConfeccion = fechaC;
        this.fechaEntrega = fechaE;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {this.id = id;}

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}

    public int getCantidad() {return cantidad;}
    public void setCantidad(int cantidad) {this.cantidad = cantidad;}

    public String getDetalle() {return detalle;}
    public void setDetalle(String detalle) {this.detalle = detalle;}

    public Medicamento getMedicamento() {
        return medicamento;
    }
    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public LocalDate getFechaConfeccion() {return fechaConfeccion;}
    public void setFechaConfeccion(LocalDate fechaConfeccion) {this.fechaConfeccion = fechaConfeccion;}

    public LocalDate getFechaEntrega() {return fechaEntrega;}
    public void setFechaEntrega(LocalDate fechaEntrega) {this.fechaEntrega = fechaEntrega;}

    public Paciente getPaciente() {
        return paciente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getNombrePaciente() {
        return paciente.getNombre();
    }
    public String getNombreMedicamento() {return medicamento.getNombre();}

    public static void reiniciarSEQ(int nextValue) {
        SEQ.set(nextValue);
    }
}
