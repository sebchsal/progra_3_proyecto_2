package org.example.sistemasrecetasbd_v.Logica.Mapper;

import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.PacienteEntity;

public class PacienteMapper {

    public static PacienteEntity toXml(Paciente p) {
        if (p == null) return null;
        PacienteEntity x = new PacienteEntity();
        x.setId(p.getId());
        x.setIdentificacion(p.getIdentificacion());
        x.setNombre(p.getNombre());
        x.setFechaNacimiento(p.getFechaNacimiento());
        x.setTelefono(p.getTelefono());
        return x;
    }

    public static Paciente toModel(PacienteEntity x) {
        if (x == null) return null;
        return new Paciente(
                x.getId(),
                x.getIdentificacion(),
                x.getNombre(),
                x.getFechaNacimiento(),
                x.getTelefono()
        );
    }
}
