package org.example.sistemarecetas.Model.Listas;

import org.example.sistemarecetas.Model.Clases.Farmaceuta;

import java.util.Optional;

public class ListaFarmaceutas extends ListaBase<Farmaceuta> {
    public ListaFarmaceutas() {
        super(Farmaceuta::getId);
    }

    public boolean verificarClave(int id, String clave) {
        return buscarPorId(id)
                .map(f -> {
                    String c = f.getClave();
                    String q = (clave == null) ? "" : clave;
                    return c != null && c.equals(q);
                }).orElse(false);
    }

    public boolean actualizarClave(int id, String nuevaClave) {
        if (nuevaClave == null || nuevaClave.isBlank()) return false;
        for (Farmaceuta f : getItems()) {
            if (f.getId() == id) {
                f.setClave(nuevaClave.trim());
                return true;
            }
        }
        return false;
    }

    public Optional<Farmaceuta> buscarPorIdentificacion(String identificacion) {
        if (identificacion == null) return Optional.empty();
        String needle = identificacion.trim();
        if (needle.isEmpty()) return Optional.empty();

        for (Farmaceuta f : getItems()) {
            if (f == null) continue;
            String cur = f.getIdentificacion();
            if (cur != null && cur.equalsIgnoreCase(needle)) {return Optional.of(f);}
        }
        return Optional.empty();
    }
}
