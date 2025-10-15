package org.example.sistemarecetas.Persistencia.Conector;

import jakarta.xml.bind.annotation.*;
import org.example.sistemarecetas.Model.Listas.ListaMedicos;
import org.example.sistemarecetas.Model.Clases.Medico;
import org.example.sistemarecetas.Persistencia.Entity.MedicoEntity;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "medicosData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicoConector {

    @XmlElementWrapper(name = "medicos")
    @XmlElement(name = "medico")
    private List<MedicoEntity> medicos = new ArrayList<>();

    public List<MedicoEntity> getMedicos() { return medicos; }
    public void setMedicos(List<MedicoEntity> medicos) { this.medicos = medicos; }

    public ListaMedicos toListaMedicos() {
        ListaMedicos lista = new ListaMedicos();

        for (MedicoEntity e : medicos) {
            Medico m = new Medico(e.getId(), e.getIdentificacion(), e.getNombre(), e.getClave(), e.getEspecialidad());
            lista.agregarOReemplazar(m);
        }

        // Ajustar el contador después de cargar
        int maxId = lista.getItems().stream()
                .mapToInt(Medico::getId)
                .max()
                .orElse(0);
        Medico.resetSequenceTo(maxId == 0 ? 0 : maxId + 1);

        return lista;
    }

    public static MedicoConector fromListaMedicos(ListaMedicos lista) {
        MedicoConector wrapper = new MedicoConector();
        for (Medico m : lista.getItems()) {
            MedicoEntity e = new MedicoEntity();
            e.setId(m.getId());
            e.setIdentificacion(m.getIdentificacion());
            e.setNombre(m.getNombre());
            e.setClave(m.getClave());
            e.setEspecialidad(m.getEspecialidad());
            wrapper.getMedicos().add(e);
        }
        return wrapper;
    }
}
