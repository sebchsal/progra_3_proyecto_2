package org.example.sistemarecetas.Logica.Logica;

import org.example.sistemarecetas.Logica.Mapper.PacienteMapper;
import org.example.sistemarecetas.Persistencia.Conector.PacienteConector;
import org.example.sistemarecetas.Persistencia.Datos.PacienteDatos;
import org.example.sistemarecetas.Persistencia.Entity.PacienteEntity;
import org.example.sistemarecetas.Model.Clases.Paciente;

import java.util.*;
import java.util.stream.Collectors;

public class PacienteLogica {
    private final PacienteDatos store;

    public PacienteLogica(String rutaArchivo) {
        this.store = new PacienteDatos(rutaArchivo);
    }

    public List<Paciente> findAll() {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .map(PacienteMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Paciente> findById(int id) {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .map(PacienteMapper::toModel);
    }

    public List<Paciente> searchByNombreOIdentificacion(String texto) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .filter(x -> (x.getNombre() != null && x.getNombre().toLowerCase().contains(q)) ||
                        (x.getIdentificacion() != null && x.getIdentificacion().toLowerCase().contains(q)) ||
                        (x.getTelefono() != null && x.getTelefono().toLowerCase().contains(q)))
                .map(PacienteMapper::toModel)
                .collect(Collectors.toList());
    }

    public Paciente create(Paciente nuevo) {
        validarNuevo(nuevo);
        PacienteConector data = store.load();

        boolean existsIdent = data.getPacientes().stream()
                .anyMatch(x -> nuevo.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
        if (existsIdent) throw new IllegalArgumentException("Ya existe un paciente con esa identificación.");

        PacienteEntity x = PacienteMapper.toXml(nuevo);
        data.getPacientes().add(x);
        store.save(data);
        return PacienteMapper.toModel(x);
    }

    public Paciente update(Paciente paciente) {
        if (paciente == null || paciente.getId() <= 0)
            throw new IllegalArgumentException("El paciente a actualizar requiere un ID válido.");
        validarCampos(paciente);

        PacienteConector data = store.load();

        boolean conflict = data.getPacientes().stream()
                .anyMatch(x -> x.getId() != paciente.getId()
                        && paciente.getIdentificacion().equalsIgnoreCase(x.getIdentificacion()));
        if (conflict) throw new IllegalArgumentException("Otro paciente ya tiene esa identificación.");

        for (int i = 0; i < data.getPacientes().size(); i++) {
            PacienteEntity actual = data.getPacientes().get(i);
            if (actual.getId() == paciente.getId()) {
                PacienteEntity actualizado = PacienteMapper.toXml(paciente);
                actualizado.setId(actual.getId());
                data.getPacientes().set(i, actualizado);
                store.save(data);
                return paciente;
            }
        }
        throw new NoSuchElementException("No existe paciente con id: " + paciente.getId());
    }

    public boolean deleteById(int id) {
        if (id <= 0) return false;
        PacienteConector data = store.load();
        boolean removed = data.getPacientes().removeIf(x -> x.getId() == id);
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------
    private void validarNuevo(Paciente p) {
        if (p == null) throw new IllegalArgumentException("Paciente nulo.");
        validarCampos(p);
    }

    private void validarCampos(Paciente p) {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (p.getIdentificacion() == null || p.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria.");

    }
}
