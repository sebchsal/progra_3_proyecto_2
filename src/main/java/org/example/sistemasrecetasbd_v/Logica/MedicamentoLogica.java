package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Data.MedicamentoDatos;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

import java.sql.SQLException;
import java.util.List;

public class MedicamentoLogica {

    private final MedicamentoDatos medicamentoDatos = new MedicamentoDatos();

    public Medicamento insert(Medicamento medicamento) throws SQLException {
        validarNuevo(medicamento);
        return medicamentoDatos.insert(medicamento);
    }

    public Medicamento findById(int id) throws SQLException {
        return medicamentoDatos.findById(id);
    }

    public List<Medicamento> findAll() throws SQLException {
        return medicamentoDatos.findAll();
    }

    public Medicamento update(Medicamento medicamento) throws SQLException {
        if (medicamento == null || medicamento.getId() <= 0)
            throw new IllegalArgumentException("El medicamento a actualizar requiere un ID válido.");
        validarCampos(medicamento);
        return medicamentoDatos.update(medicamento);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) return false;
        return medicamentoDatos.delete(id) > 0;
    }

    public void reiniciarSecuencia() throws SQLException {
        MedicamentoDatos datos = new MedicamentoDatos();
        int ultimoId = datos.obtenerUltimoId();
        Medicamento.reiniciarSEQ(ultimoId);
    }

    // --------- Helpers ---------

    private void validarNuevo(Medicamento md) {
        if (md == null) throw new IllegalArgumentException("Medicamento nulo.");
        validarCampos(md);
    }

    private void validarCampos(Medicamento md) {
        if(md.getCodigo() == null || md.getCodigo().isBlank())
            throw new IllegalArgumentException("El codigo es obligatorio.");
        if (md.getNombre() == null || md.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (md.getTipoPresentacion() == null || md.getTipoPresentacion().isBlank())
            throw new IllegalArgumentException("El tipo presentacion es obligatorio.");
    }
}


