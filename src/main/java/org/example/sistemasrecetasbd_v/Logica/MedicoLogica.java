package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Data.MedicoDatos;
import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MedicoLogica {
    
    private final MedicoDatos medicoDatos = new MedicoDatos();

    public Medico insert(Medico medico) throws SQLException {
        validarNuevo(medico);
        return medicoDatos.insert(medico);
    }

    public Medico findById(int id) throws SQLException {
        return medicoDatos.findById(id);
    }

    public List<Medico> findAll() throws SQLException {
        return medicoDatos.findAll();
    }

    public Medico update(Medico medico) throws SQLException {
        if (medico == null || medico.getId() <= 0)
            throw new IllegalArgumentException("El medico a actualizar requiere un ID válido.");
        validarCampos(medico);
        return medicoDatos.update(medico);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) return false;
        return medicoDatos.delete(id) > 0;
    }

    public void reiniciarSecuencia() throws SQLException {
        MedicoDatos datos = new MedicoDatos();
        int ultimoId = datos.obtenerUltimoId();
        Medico.reiniciarSEQ(ultimoId);
    }

    // --------- Helpers ---------

    private void validarNuevo(Medico m) {
        if (m == null) throw new IllegalArgumentException("Medico nulo.");
        validarCampos(m);
    }

    private void validarCampos(Medico m) {
        if(m.getIdentificacion() == null || m.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificacion es obligatorio.");
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getEspecialidad() == null || m.getEspecialidad().isBlank())
            throw new IllegalArgumentException("La especialidad es obligatorio.");
    }
}
