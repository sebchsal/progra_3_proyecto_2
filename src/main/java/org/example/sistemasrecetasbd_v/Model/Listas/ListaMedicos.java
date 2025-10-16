package org.example.sistemasrecetasbd_v.Model.Listas;

import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ListaMedicos extends ListaBase<Medico> {

    public ListaMedicos() {
        super(Medico::getId);
    }

    public boolean verificarClave(int id, String clave) {
        return buscarPorId(id)
                .map(m -> {
                    String c = m.getClave();
                    String q = (clave == null) ? "" : clave;
                    return c != null && c.equals(q);
                }).orElse(false);
    }

    public boolean actualizarClave(int id, String nuevaClave) {
        if (nuevaClave == null || nuevaClave.isBlank()) return false;
        for (Medico m : getItems()) {
            if (m.getId() == id) {
                m.setClave(nuevaClave.trim());
                return true;
            }
        }
        return false;
    }

    public Optional<Medico> buscarPorIdentificacion(String identificacion) {
        if (identificacion == null) return Optional.empty();
        String needle = identificacion.trim();
        if (needle.isEmpty()) return Optional.empty();

        for (Medico m : getItems()) {
            if (m == null) continue;
            String cur = m.getIdentificacion();
            if (cur != null && cur.equalsIgnoreCase(needle)) {return Optional.of(m);}
        }
        return Optional.empty();
    }
}
