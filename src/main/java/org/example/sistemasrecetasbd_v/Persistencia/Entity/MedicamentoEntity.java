package org.example.sistemasrecetasbd_v.Persistencia.Entity;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MedicamentoEntity {
    private int id;
    private String codigo;
    private String nombre;
    private String tipoPresentacion;

    public MedicamentoEntity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoPresentacion() { return tipoPresentacion; }
    public void setTipoPresentacion(String tipoPresentacion) { this.tipoPresentacion = tipoPresentacion; }
}
