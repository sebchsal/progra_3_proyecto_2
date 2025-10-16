package org.example.sistemasrecetasbd_v.Model.Listas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

public class CatalogoMedicamentos extends ListaBase<Medicamento> {
    public CatalogoMedicamentos() {
        super(Medicamento::getId);
    }

    public Optional<Medicamento> buscarPorCodigo(String codigoParcial) {
        if (codigoParcial == null) {
            return Optional.empty();
        } else {
            String needle = codigoParcial.trim();
            if (needle.isEmpty()) {
                return Optional.empty();
            } else {
                for(Medicamento a : this.getItems()) {
                    if (a != null) {
                        String cur = a.getCodigo();
                        if (cur != null && cur.equalsIgnoreCase(needle)) {
                            return Optional.of(a);
                        }
                    }
                }
                return Optional.empty();
            }
        }
    }
}