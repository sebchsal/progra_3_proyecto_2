package org.example.sistemasrecetasbd_v.Model.Listas;

import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaPacientes extends ListaBase<Paciente> {

    public ListaPacientes() {
        super(Paciente::getId);
    }

    public Optional<Paciente> buscarPorIdentificacionPaciente(String identificacion) {
        if (identificacion == null) return Optional.empty();
        String needle = identificacion.trim();
        if (needle.isEmpty()) return Optional.empty();

        for (Paciente a : getItems()) {
            if (a == null) continue;
            String cur = a.getIdentificacion();
            if (cur != null && cur.equalsIgnoreCase(needle)) {return Optional.of(a);}
        }
        return Optional.empty();
    }
}
