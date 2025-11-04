package org.example.sistemasrecetasbd_v.Logica;

import org.example.sistemasrecetasbd_v.Model.Listas.HistoricoRecetas;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;

import java.time.LocalDate;

public class DespachoLogica {

    public static void validarCambiosReceta(Receta seleccionada, String nuevoEstado) {
        if (seleccionada == null) {
            throw new IllegalArgumentException("Seleccione una receta.");
        }
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("Seleccione un estado para la receta.");
        }
    }

    public static void actualizarReceta(Receta seleccionada, String nuevoEstado, LocalDate fechaConfeccion,
                                        LocalDate fechaEntrega, String detalle) {
        seleccionada.setEstado(nuevoEstado);
        if (fechaConfeccion != null) {
            seleccionada.setFechaConfeccion(fechaConfeccion);
        }
        if (fechaEntrega != null) {
            seleccionada.setFechaEntrega(fechaEntrega);
        }
        if (detalle != null) {
            seleccionada.setDetalle(detalle.trim());
        }
    }

    public static void actualizarEnHistoricoSinDuplicar(HistoricoRecetas historico, Receta receta) {
        if (historico == null || historico.getItems() == null || receta == null) {
            return;
        }
        var lista = historico.getItems();
        for (int i = 0; i < lista.size(); i++) {
            Receta current = lista.get(i);
            // Misma referencia o mismo id => reemplazar posición y salir
            if (current == receta || (current != null && java.util.Objects.equals(current.getId(), receta.getId()))) {
                lista.set(i, receta);
                return;
            }
        }
    }
}
