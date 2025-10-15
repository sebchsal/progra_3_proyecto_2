package org.example.sistemarecetas.Persistencia.Conector;

import jakarta.xml.bind.annotation.*;
import org.example.sistemarecetas.Model.Clases.Farmaceuta;
import org.example.sistemarecetas.Model.Listas.ListaFarmaceutas;
import org.example.sistemarecetas.Persistencia.Entity.FarmaceutaEntity;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "farmaceutasData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FarmaceutaConector {

    @XmlElementWrapper(name = "farmaceutas")
    @XmlElement(name = "farmaceuta")
    private List<FarmaceutaEntity> farmaceutas = new ArrayList<>();

    public List<FarmaceutaEntity> getFarmaceutas() { return farmaceutas; }
    public void setFarmaceutas(List<FarmaceutaEntity> farmaceutas) { this.farmaceutas = farmaceutas; }

    public ListaFarmaceutas toListaFarmaceutas() {
        ListaFarmaceutas lista = new ListaFarmaceutas();
        for (FarmaceutaEntity e : farmaceutas) {
            Farmaceuta f = new Farmaceuta(e.getId(), e.getIdentificacion(), e.getNombre(), e.getClave());
            lista.agregarOReemplazar(f);
        }

        // Ajustar contador
        int maxId = lista.getItems().stream()
                .mapToInt(Farmaceuta::getId)
                .max()
                .orElse(0);
        Farmaceuta.resetSequenceTo(maxId == 0 ? 0 : maxId + 1);

        return lista;
    }

    public static FarmaceutaConector fromListaFarmaceutas(ListaFarmaceutas lista) {
        FarmaceutaConector wrapper = new FarmaceutaConector();
        for (Farmaceuta f : lista.getItems()) {
            FarmaceutaEntity e = new FarmaceutaEntity();
            e.setId(f.getId());
            e.setIdentificacion(f.getIdentificacion());
            e.setNombre(f.getNombre());
            e.setClave(f.getClave());
            wrapper.getFarmaceutas().add(e);
        }
        return wrapper;
    }
}
