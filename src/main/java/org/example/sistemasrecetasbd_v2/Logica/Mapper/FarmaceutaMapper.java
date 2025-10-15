package org.example.sistemarecetas.Logica.Mapper;

import org.example.sistemarecetas.Model.Clases.Farmaceuta;
import org.example.sistemarecetas.Persistencia.Entity.FarmaceutaEntity;

public class FarmaceutaMapper {

    public static FarmaceutaEntity toXml(Farmaceuta f) {
        if (f == null) return null;
        FarmaceutaEntity x = new FarmaceutaEntity();
        x.setId(f.getId());
        x.setIdentificacion(f.getIdentificacion());
        x.setNombre(f.getNombre());
        x.setClave(f.getClave());
        return x;
    }

    public static Farmaceuta toModel(FarmaceutaEntity x) {
        if (x == null) return null;
        return new Farmaceuta(
                x.getId(),
                x.getIdentificacion(),
                x.getNombre(),
                x.getClave()
        );
    }
}
