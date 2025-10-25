package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Data.FarmaceutaDatos;
import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;
import java.sql.SQLException;
import java.util.List;

public class FarmaceutaLogica {

    private final FarmaceutaDatos farmaDatos = new FarmaceutaDatos();

    public Farmaceuta insert(Farmaceuta farmaceuta) throws SQLException {
        validarNuevo(farmaceuta);
        return farmaDatos.insert(farmaceuta);
    }

    public Farmaceuta findById(int id) throws SQLException {
        return farmaDatos.findById(id);
    }

    public List<Farmaceuta> findAll() throws SQLException {
        return farmaDatos.findAll();
    }

    public Farmaceuta update(Farmaceuta farmaceuta) throws SQLException {
        if (farmaceuta == null || farmaceuta.getId() <= 0)
            throw new IllegalArgumentException("El cliente a actualizar requiere un ID válido.");
        validarCampos(farmaceuta);
        return farmaDatos.update(farmaceuta);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) return false;
        return farmaDatos.delete(id) > 0;
    }

    public void reiniciarSecuencia() throws SQLException {
        FarmaceutaDatos datos = new FarmaceutaDatos();
        int ultimoId = datos.obtenerUltimoId();
        Farmaceuta.reiniciarSEQ(ultimoId);
    }

    // --------- Helpers ---------

    private void validarNuevo(Farmaceuta f) {
        if (f == null) throw new IllegalArgumentException("Medico nulo.");
        validarCampos(f);
    }

    private void validarCampos(Farmaceuta f) {
        if(f.getIdentificacion() == null || f.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificacion es obligatorio.");
        if (f.getNombre() == null || f.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
    }
}
