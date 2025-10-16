package org.example.sistemasrecetasbd_v.Logica.Logica;

import org.example.sistemasrecetasbd_v.Logica.Mapper.RecetaMapper;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Persistencia.Conector.RecetaConector;
import org.example.sistemasrecetasbd_v.Persistencia.Datos.RecetaDatos;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.RecetaEntity;

import java.util.*;
import java.util.stream.Collectors;

public class RecetaLogica {
    private final RecetaDatos store;

    public RecetaLogica(String rutaArchivo) {
        this.store = new RecetaDatos(rutaArchivo);
    }

    public List<Receta> findAll() {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Receta> findById(int id) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .map(RecetaMapper::toModel);
    }

    public Receta create(Receta nueva) {
        RecetaConector data = store.load();

        RecetaEntity x = RecetaMapper.toXml(nueva);
        data.getRecetas().add(x);
        store.save(data);
        return RecetaMapper.toModel(x);
    }

    public Receta update(Receta receta) {
        if (receta == null || receta.getId() <= 0)
            throw new IllegalArgumentException("La receta a actualizar requiere un ID válido.");

        RecetaConector data = store.load();

        for (int i = 0; i < data.getRecetas().size(); i++) {
            RecetaEntity actual = data.getRecetas().get(i);
            if (actual.getId() == receta.getId()) {
                RecetaEntity actualizado = RecetaMapper.toXml(receta);
                actualizado.setId(actual.getId());
                data.getRecetas().set(i, actualizado);
                store.save(data);
                return receta;
            }
        }
        throw new NoSuchElementException("No existe receta con id: " + receta.getId());
    }

    public boolean deleteById(int id) {
        if (id <= 0) return false;
        RecetaConector data = store.load();
        boolean removed = data.getRecetas().removeIf(x -> x.getId() == id);
        if (removed) store.save(data);
        return removed;
    }
}