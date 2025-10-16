package org.example.sistemasrecetasbd_v.Persistencia.Conector;

import jakarta.xml.bind.annotation.*;
import org.example.sistemasrecetasbd_v.Model.Listas.ListaPacientes;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.PacienteEntity;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "pacientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class PacienteConector {

    @XmlElementWrapper(name = "pacientes")
    @XmlElement(name = "paciente")
    private List<PacienteEntity> pacientes = new ArrayList<>();

    public List<PacienteEntity> getPacientes() { return pacientes; }
    public void setPacientes(List<PacienteEntity> pacientes) { this.pacientes = pacientes; }

    public ListaPacientes toListaPacientes() {
        ListaPacientes lista = new ListaPacientes();

        for (PacienteEntity e : pacientes) {
            Paciente p = new Paciente(
                    e.getId(),
                    e.getIdentificacion(),
                    e.getNombre(),
                    e.getFechaNacimiento(),
                    e.getTelefono()
            );
            lista.agregarOReemplazar(p);
        }

        int maxId = lista.getItems().stream()
                .mapToInt(Paciente::getId)
                .max()
                .orElse(0);
        Paciente.resetSequenceTo(maxId == 0 ? 0 : maxId + 1);

        return lista;
    }

    public static PacienteConector fromListaPacientes(ListaPacientes lista) {
        PacienteConector wrapper = new PacienteConector();
        for (Paciente p : lista.getItems()) {
            PacienteEntity e = new PacienteEntity();
            e.setId(p.getId());
            e.setIdentificacion(p.getIdentificacion());
            e.setNombre(p.getNombre());
            e.setFechaNacimiento(p.getFechaNacimiento());
            e.setTelefono(p.getTelefono());
            wrapper.getPacientes().add(e);
        }
        return wrapper;
    }
}
