package org.example.sistemarecetas.Logica.Logica;

import org.example.sistemarecetas.Logica.Mapper.MedicamentoMapper;
import org.example.sistemarecetas.Persistencia.Conector.MedicamentoConector;
import org.example.sistemarecetas.Persistencia.Datos.MedicamentoDatos;
import org.example.sistemarecetas.Persistencia.Entity.MedicamentoEntity;
import org.example.sistemarecetas.Model.Clases.Medicamento;

import java.util.*;
import java.util.stream.Collectors;

public class MedicamentoLogica {
    private final MedicamentoDatos store;

    public MedicamentoLogica(String rutaArchivo) {
        this.store = new MedicamentoDatos(rutaArchivo);
    }

    public List<Medicamento> findAll() {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Medicamento> findById(int id) {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .map(MedicamentoMapper::toModel);
    }

    public List<Medicamento> searchByNombreOCodigo(String texto) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x ->
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(q)) ||
                                (x.getCodigo() != null && x.getCodigo().toLowerCase().contains(q)) ||
                                (x.getTipoPresentacion() != null && x.getTipoPresentacion().toLowerCase().contains(q))
                )
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Medicamento create(Medicamento nuevo) {
        validarNuevo(nuevo);
        MedicamentoConector data = store.load();

        boolean existsCodigo = data.getMedicamentos().stream()
                .anyMatch(x -> nuevo.getCodigo().equalsIgnoreCase(x.getCodigo()));
        if (existsCodigo) throw new IllegalArgumentException("Ya existe un medicamento con ese código.");

        MedicamentoEntity x = MedicamentoMapper.toXml(nuevo);
        data.getMedicamentos().add(x);
        store.save(data);
        return MedicamentoMapper.toModel(x);
    }

    public Medicamento update(Medicamento medicamento) {
        if (medicamento == null || medicamento.getId() <= 0)
            throw new IllegalArgumentException("El medicamento a actualizar requiere un ID válido.");
        validarCampos(medicamento);

        MedicamentoConector data = store.load();

        boolean conflict = data.getMedicamentos().stream()
                .anyMatch(x -> x.getId() != medicamento.getId()
                        && medicamento.getCodigo().equalsIgnoreCase(x.getCodigo()));
        if (conflict) throw new IllegalArgumentException("Otro medicamento ya tiene ese código.");

        for (int i = 0; i < data.getMedicamentos().size(); i++) {
            MedicamentoEntity actual = data.getMedicamentos().get(i);
            if (actual.getId() == medicamento.getId()) {
                MedicamentoEntity actualizado = MedicamentoMapper.toXml(medicamento);
                actualizado.setId(actual.getId());
                data.getMedicamentos().set(i, actualizado);
                store.save(data);
                return medicamento;
            }
        }
        throw new NoSuchElementException("No existe medicamento con id: " + medicamento.getId());
    }

    public boolean deleteById(int id) {
        if (id <= 0) return false;
        MedicamentoConector data = store.load();
        boolean removed = data.getMedicamentos().removeIf(x -> x.getId() == id);
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------
    private void validarNuevo(Medicamento m) {
        if (m == null) throw new IllegalArgumentException("Medicamento nulo.");
        validarCampos(m);
    }

    private void validarCampos(Medicamento m) {
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getCodigo() == null || m.getCodigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");
        if (m.getTipoPresentacion() == null || m.getTipoPresentacion().isBlank())
            throw new IllegalArgumentException("La presentación es obligatoria.");
    }
}
