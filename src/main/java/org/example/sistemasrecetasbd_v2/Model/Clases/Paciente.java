package org.example.sistemarecetas.Model.Clases;

import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.atomic.AtomicInteger;

public class Paciente {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final int id;
    private String nombre;
    private String identificacion;
    private int edad;
    private LocalDate fechaNacimiento;
    private String telefono;

    public Paciente(String identificacion, String nombre, LocalDate fechaNacimiento, String telefono) {
        this.id = SEQ.getAndIncrement();
        this.nombre = nombre;
        this.identificacion = identificacion;
        setFechaNacimiento(fechaNacimiento);
        this.telefono = telefono;
    }

    // Constructor especial para XML
    public Paciente(int id, String identificacion, String nombre, LocalDate fechaNacimiento, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.identificacion = identificacion;
        setFechaNacimiento(fechaNacimiento); // calcula y valida edad
        this.telefono = telefono;

        // sincroniza la secuencia con el ID leído
        SEQ.updateAndGet(cur -> Math.max(cur, id + 1));
    }

    public String getIdentificacion() {
        return identificacion;
    }
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() { return id; }

    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        int edadPaciente = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        this.fechaNacimiento = fechaNacimiento;
        setEdad(edadPaciente);
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public static void resetSequenceTo(int nextValue) {
        SEQ.set(nextValue);
    }
}
