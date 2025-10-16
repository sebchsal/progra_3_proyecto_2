package org.example.sistemasrecetasbd_v.Persistencia.Conector;

import jakarta.xml.bind.annotation.*;
import org.example.sistemasrecetasbd_v.Model.Listas.CatalogoMedicamentos;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.MedicamentoEntity;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "medicamentosData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicamentoConector {

    @XmlElementWrapper(name = "medicamentos")
    @XmlElement(name = "medicamento")
    private List<MedicamentoEntity> medicamentos = new ArrayList<>();

    public List<MedicamentoEntity> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<MedicamentoEntity> medicamentos) { this.medicamentos = medicamentos; }

    public CatalogoMedicamentos toCatalogoMedicamentos() {
        CatalogoMedicamentos catalogo = new CatalogoMedicamentos();

        for (MedicamentoEntity e : medicamentos) {
            Medicamento m = new Medicamento(
                    e.getId(),
                    e.getCodigo(),
                    e.getNombre(),
                    e.getTipoPresentacion()
            );
            catalogo.agregarOReemplazar(m);
        }

        int maxId = catalogo.getItems().stream()
                .mapToInt(Medicamento::getId)
                .max()
                .orElse(0);
        Medicamento.resetSequenceTo(maxId == 0 ? 0 : maxId + 1);

        return catalogo;
    }

    public static MedicamentoConector fromCatalogoMedicamentos(CatalogoMedicamentos catalogo) {
        MedicamentoConector wrapper = new MedicamentoConector();
        for (Medicamento m : catalogo.getItems()) {
            MedicamentoEntity e = new MedicamentoEntity();
            e.setId(m.getId());
            e.setCodigo(m.getCodigo());
            e.setNombre(m.getNombre());
            e.setTipoPresentacion(m.getTipoPresentacion());
            wrapper.getMedicamentos().add(e);
        }
        return wrapper;
    }
}
