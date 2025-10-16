package org.example.sistemasrecetasbd_v.Logica.Mapper;

import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.MedicamentoEntity;

public class MedicamentoMapper {

    public static MedicamentoEntity toXml(Medicamento m) {
        if (m == null) return null;
        MedicamentoEntity x = new MedicamentoEntity();
        x.setId(m.getId());
        x.setCodigo(m.getCodigo());
        x.setNombre(m.getNombre());
        x.setTipoPresentacion(m.getTipoPresentacion());
        return x;
    }

    public static Medicamento toModel(MedicamentoEntity x) {
        if (x == null) return null;
        return new Medicamento(
                x.getId(),
                x.getCodigo(),
                x.getNombre(),
                x.getTipoPresentacion()
        );
    }
}
