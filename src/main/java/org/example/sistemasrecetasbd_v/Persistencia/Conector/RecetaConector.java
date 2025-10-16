package org.example.sistemasrecetasbd_v.Persistencia.Conector;

import jakarta.xml.bind.annotation.*;
import org.example.sistemasrecetasbd_v.Model.Listas.HistoricoRecetas;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.RecetaEntity;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recetasData")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaConector {

    @XmlElementWrapper(name = "recetas")
    @XmlElement(name = "receta")
    private List<RecetaEntity> recetas = new ArrayList<>();

    public List<RecetaEntity> getRecetas() { return recetas; }
    public void setRecetas(List<RecetaEntity> recetas) { this.recetas = recetas; }

    public HistoricoRecetas toListaRecetas() {
        HistoricoRecetas lista = new HistoricoRecetas();
        for (RecetaEntity e : recetas) {
            Receta r = org.example.sistemasrecetasbd_v.Logica.Mapper.RecetaMapper.toModel(e);
            lista.agregarOReemplazar(r);
        }

        int maxId = lista.getItems().stream()
                .mapToInt(Receta::getId)
                .max()
                .orElse(0);
        Receta.resetSequenceTo(maxId == 0 ? 0 : maxId + 1);

        return lista;
    }

    public static RecetaConector fromListaRecetas(HistoricoRecetas lista) {
        RecetaConector wrapper = new RecetaConector();
        for (Receta r : lista.getItems()) {
            RecetaEntity e = org.example.sistemasrecetasbd_v.Logica.Mapper.RecetaMapper.toXml(r);
            wrapper.getRecetas().add(e);
        }
        return wrapper;
    }
}