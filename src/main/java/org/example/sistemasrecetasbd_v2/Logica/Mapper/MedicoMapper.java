package org.example.sistemarecetas.Logica.Mapper;

import org.example.sistemarecetas.Persistencia.Entity.MedicoEntity;
import org.example.sistemarecetas.Model.Clases.Medico;

public class MedicoMapper {

    public static MedicoEntity toXml(Medico m) {
        if (m == null) return null;
        MedicoEntity x = new MedicoEntity();
        x.setId(m.getId());
        x.setIdentificacion(m.getIdentificacion());
        x.setNombre(m.getNombre());
        x.setClave(m.getClave());
        x.setEspecialidad(m.getEspecialidad());
        return x;
    }

    public static Medico toModel(MedicoEntity x) {
        if (x == null) return null;
        return new Medico(
                x.getId(),
                x.getIdentificacion(),
                x.getNombre(),
                x.getClave(),
                x.getEspecialidad()
        );
    }
}