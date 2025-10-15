package org.example.sistemarecetas.Persistencia.Entity;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class FarmaceutaEntity {
    private int id;
    private String identificacion;
    private String nombre;
    private String clave;

    public FarmaceutaEntity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
