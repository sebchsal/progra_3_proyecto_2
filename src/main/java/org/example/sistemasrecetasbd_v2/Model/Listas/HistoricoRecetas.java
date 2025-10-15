package org.example.sistemarecetas.Model.Listas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.example.sistemarecetas.Model.Clases.Receta;

public class HistoricoRecetas extends ListaBase<Receta> {
    public HistoricoRecetas() {
        super(Receta::getId);
    }

    public List<Receta> porPacienteId(String pacienteId) {
        String q = pacienteId == null ? "" : pacienteId.trim();
        List<Receta> resultado = new ArrayList();
        boolean numeric = q.matches("\\d+");

        for(Receta r : this.getItems()) {
            if (r != null && r.getPaciente() != null) {
                int pid = r.getPaciente().getId();
                if (numeric && pid == Integer.parseInt(q) && String.valueOf(pid).contains(q)) {
                    resultado.add(r);
                }
            }
        }
        return resultado;
    }

    public List<Receta> porRangoFechaConfeccion(LocalDate desde, LocalDate hasta) {
        List<Receta> resultado = new ArrayList();

        for(Receta r : this.getItems()) {
            if (r != null) {
                LocalDate f = r.getFechaConfeccion();
                if (f != null) {
                    boolean afterEq = desde == null || !f.isBefore(desde);
                    boolean beforeEq = hasta == null || !f.isAfter(hasta);
                    if (afterEq && beforeEq) {
                        resultado.add(r);
                    }
                }
            }
        }

        return resultado;
    }
}

