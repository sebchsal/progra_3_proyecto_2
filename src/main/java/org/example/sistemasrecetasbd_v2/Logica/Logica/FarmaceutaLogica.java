package org.example.sistemarecetas.Logica.Logica;

import org.example.sistemarecetas.Logica.Mapper.FarmaceutaMapper;
import org.example.sistemarecetas.Persistencia.Conector.FarmaceutaConector;
import org.example.sistemarecetas.Persistencia.Datos.FarmaceutaDatos;
import org.example.sistemarecetas.Persistencia.Entity.FarmaceutaEntity;
import org.example.sistemarecetas.Model.Clases.Farmaceuta;

import java.util.*;
import java.util.stream.Collectors;

public class FarmaceutaLogica {
    private final FarmaceutaDatos store;

    public FarmaceutaLogica(String rutaArchivo) {
        this.store = new FarmaceutaDatos(rutaArchivo);
    }

    public List<Farmaceuta> findAll() {
        FarmaceutaConector data = store.load();
        return data.getFarmaceutas().stream()
                .map(FarmaceutaMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Farmaceuta> findById(int id) {
        FarmaceutaConector data = store.load();
        return data.getFarmaceutas().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .map(FarmaceutaMapper::toModel);
    }

    public List<Farmaceuta> searchByNombreOIdentificacion(String texto) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();
        FarmaceutaConector data = store.load();
        return data.getFarmaceutas().stream()
                .filter(x -> (x.getNombre() != null && x.getNombre().toLowerCase().contains(q))
                        || (x.getIdentificacion() != null && x.getIdentificacion().toLowerCase().contains(q)))
                .map(FarmaceutaMapper::toModel)
                .collect(Collectors.toList());
    }

    public Farmaceuta create(Farmaceuta nuevo) {
        validarNuevo(nuevo);
        FarmaceutaConector data = store.load();

        boolean existsIdent = data.getFarmaceutas().stream()
                .anyMatch(x -> nuevo.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
        if (existsIdent) throw new IllegalArgumentException("Ya existe un farmaceuta con esa identificación.");

        if (data.getFarmaceutas().isEmpty()) {
            Farmaceuta.resetSequenceTo(1);
        }

        FarmaceutaEntity x = FarmaceutaMapper.toXml(nuevo);
        data.getFarmaceutas().add(x);
        store.save(data);
        return FarmaceutaMapper.toModel(x);
    }

    public Farmaceuta update(Farmaceuta farmaceuta) {
        if (farmaceuta == null || farmaceuta.getId() <= 0)
            throw new IllegalArgumentException("El farmaceuta a actualizar requiere un ID válido.");
        validarCampos(farmaceuta);

        FarmaceutaConector data = store.load();

        boolean conflict = data.getFarmaceutas().stream()
                .anyMatch(x -> x.getId() != farmaceuta.getId()
                        && farmaceuta.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
        if (conflict) throw new IllegalArgumentException("Otro farmaceuta ya tiene esa identificación.");

        for (int i = 0; i < data.getFarmaceutas().size(); i++) {
            FarmaceutaEntity actual = data.getFarmaceutas().get(i);
            if (actual.getId() == farmaceuta.getId()) {
                // 🔹 El mapper ya se encarga de copiar el ID correctamente
                FarmaceutaEntity actualizado = FarmaceutaMapper.toXml(farmaceuta);
                data.getFarmaceutas().set(i, actualizado);
                store.save(data);
                return farmaceuta;
            }
        }
        throw new NoSuchElementException("No existe farmaceuta con id: " + farmaceuta.getId());
    }


    public boolean deleteById(int id) {
        if (id <= 0) return false;
        FarmaceutaConector data = store.load();
        boolean removed = data.getFarmaceutas().removeIf(x -> x.getId() == id);
        if (removed) {
            store.save(data);
            if (data.getFarmaceutas().isEmpty()) {
                Farmaceuta.resetSequenceTo(1);
            }
        }
        return removed;
    }

    // --------- Helpers ---------
    private void validarNuevo(Farmaceuta f) {
        if (f == null) throw new IllegalArgumentException("Farmaceuta nulo.");
        validarCampos(f);
    }

    private void validarCampos(Farmaceuta f) {
        if (f.getNombre() == null || f.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (f.getIdentificacion() == null || f.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (f.getClave() == null || f.getClave().isBlank())
            throw new IllegalArgumentException("La clave es obligatoria.");
    }
}
