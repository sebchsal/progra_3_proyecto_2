package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Data.RecetaDatos;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RecetaLogica {

    private final RecetaDatos recetaDatos = new RecetaDatos();

    public Receta insert(Receta receta) throws SQLException {
        validarNuevo(receta);
        return recetaDatos.insert(receta);
    }

    public Receta findById(int id) throws SQLException {
        return recetaDatos.findById(id);
    }

    public List<Receta> findAll() throws SQLException {
        return recetaDatos.findAll();
    }

    public Receta update(Receta receta) throws SQLException {
        if (receta == null || receta.getId() <= 0)
            throw new IllegalArgumentException("El receta a actualizar requiere un ID válido.");
        validarCampos(receta);
        return recetaDatos.update(receta);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) return false;
        return recetaDatos.delete(id) > 0;
    }

    // --------- Helpers ---------

    private void validarNuevo(Receta md) {
        if (md == null) throw new IllegalArgumentException("Receta nulo.");
        validarCampos(md);
    }

    private void validarCampos(Receta r) {
        if(r.getEstado() == null || r.getEstado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio.");
        if (r.getCantidad() <=0)
            throw new IllegalArgumentException("La cantidad debe ser < 0.");
        if (r.getMedicamento() == null)
            throw new IllegalArgumentException("Debe seleccionar un medicamento antes de guardar la receta.");
        if (r.getPaciente() == null)
            throw new IllegalArgumentException("Debe seleccionar un paciente antes de guardar la receta.");
        if (r.getFechaConfeccion() != null && r.getFechaConfeccion().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Fecha de confeccion no puede ser anterior a la actual.");

    }
}