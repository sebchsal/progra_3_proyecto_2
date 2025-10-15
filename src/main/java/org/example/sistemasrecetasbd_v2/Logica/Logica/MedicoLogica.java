package org.example.sistemarecetas.Logica.Logica;

import org.example.sistemarecetas.Logica.Mapper.MedicoMapper;
import org.example.sistemarecetas.Persistencia.Conector.MedicoConector;
import org.example.sistemarecetas.Persistencia.Datos.MedicoDatos;
import org.example.sistemarecetas.Persistencia.Entity.MedicoEntity;
import org.example.sistemarecetas.Model.Clases.Medico;

import java.util.*;
import java.util.stream.Collectors;

public class MedicoLogica {
    private final MedicoDatos store;

    public MedicoLogica(String rutaArchivo) {
        this.store = new MedicoDatos(rutaArchivo);
    }

    public List<Medico> findAll() {
        MedicoConector data = store.load();
        return data.getMedicos().stream()
                .map(MedicoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Medico> findById(int id) {
        MedicoConector data = store.load();
        return data.getMedicos().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .map(MedicoMapper::toModel);
    }

    public List<Medico> searchByNombreOIdentificacion(String texto) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();
        MedicoConector data = store.load();
        return data.getMedicos().stream()
                .filter(x ->
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(q)) ||
                                (x.getIdentificacion() != null && x.getIdentificacion().toLowerCase().contains(q)) ||
                                (x.getEspecialidad() != null && x.getEspecialidad().toLowerCase().contains(q))
                )
                .map(MedicoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Medico create(Medico nuevo) {
        validarNuevo(nuevo);
        MedicoConector data = store.load();

        // Unicidad por identificación
        if (nuevo.getIdentificacion() != null && !nuevo.getIdentificacion().isBlank()) {
            boolean existsIdent = data.getMedicos().stream()
                    .anyMatch(x -> nuevo.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
            if (existsIdent) throw new IllegalArgumentException("Ya existe un médico con esa identificación.");
        }

        MedicoEntity x = MedicoMapper.toXml(nuevo);
        data.getMedicos().add(x);
        store.save(data);
        return MedicoMapper.toModel(x);
    }

    public Medico update(Medico medico) {
        if (medico == null || medico.getId() <= 0)
            throw new IllegalArgumentException("El médico a actualizar requiere un ID válido.");
        validarCampos(medico);

        MedicoConector data = store.load();

        // Validar identificación duplicada
        if (medico.getIdentificacion() != null && !medico.getIdentificacion().isBlank()) {
            boolean conflict = data.getMedicos().stream()
                    .anyMatch(x -> x.getId() != medico.getId()
                            && medico.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
            if (conflict) throw new IllegalArgumentException("Otro médico ya tiene esa identificación.");
        }

        for (int i = 0; i < data.getMedicos().size(); i++) {
            MedicoEntity actual = data.getMedicos().get(i);
            if (actual.getId() == medico.getId()) {
                MedicoEntity actualizado = MedicoMapper.toXml(medico);
                actualizado.setId(actual.getId());
                data.getMedicos().set(i, actualizado);
                store.save(data);
                return medico;
            }
        }
        throw new NoSuchElementException("No existe médico con id: " + medico.getId());
    }

    public boolean deleteById(int id) {
        if (id <= 0) return false;
        MedicoConector data = store.load();
        boolean removed = data.getMedicos().removeIf(x -> x.getId() == id);
        if (removed) store.save(data);
        return removed;
    }


    // --------- Helpers ---------
    private void validarNuevo(Medico m) {
        if (m == null) throw new IllegalArgumentException("Médico nulo.");
        validarCampos(m);
    }

    private void validarCampos(Medico m) {
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getIdentificacion() == null || m.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (m.getEspecialidad() == null || m.getEspecialidad().isBlank())
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        if (m.getClave() == null || m.getClave().isBlank())
            throw new IllegalArgumentException("La clave es obligatoria.");
    }

    private int generarSiguienteId(MedicoConector data) {
        return data.getMedicos().stream()
                .mapToInt(MedicoEntity::getId)
                .max()
                .orElse(0) + 1;
    }
}
