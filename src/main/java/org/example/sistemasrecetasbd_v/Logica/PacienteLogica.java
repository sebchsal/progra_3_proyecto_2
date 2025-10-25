package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Data.MedicamentoDatos;
import org.example.sistemasrecetasbd_v.Data.PacienteDatos;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PacienteLogica {

    private final PacienteDatos pacienteDatos = new PacienteDatos();

    public Paciente insert(Paciente paciente) throws SQLException {
        validarNuevo(paciente);
        return pacienteDatos.insert(paciente);
    }

    public Paciente findById(int id) throws SQLException {
        return pacienteDatos.findById(id);
    }

    public List<Paciente> findAll() throws SQLException {
        return pacienteDatos.findAll();
    }

    public Paciente update(Paciente paciente) throws SQLException {
        if (paciente == null || paciente.getId() <= 0)
            throw new IllegalArgumentException("El paciente a actualizar requiere un ID válido.");
        validarCampos(paciente);
        return pacienteDatos.update(paciente);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) return false;
        return pacienteDatos.delete(id) > 0;
    }

    public void reiniciarSecuencia() throws SQLException {
        PacienteDatos datos = new PacienteDatos();
        int ultimoId = datos.obtenerUltimoId();
        Paciente.reiniciarSEQ(ultimoId);
    }

    // --------- Helpers ---------

    private void validarNuevo(Paciente p) {
        if (p == null) throw new IllegalArgumentException("Paciente nulo.");
        validarCampos(p);
    }

    private void validarCampos(Paciente p) {
        if(p.getIdentificacion() == null || p.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificacion es obligatorio.");
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (p.getFechaNacimiento() != null && p.getFechaNacimiento().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Fecha nacimiento no valida.");
        if (p.getTelefono() == null || p.getTelefono().isBlank())
            throw new IllegalArgumentException("El telefono es obligatorio.");
    }
}

