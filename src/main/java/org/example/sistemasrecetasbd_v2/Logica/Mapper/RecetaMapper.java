package org.example.sistemarecetas.Logica.Mapper;

import org.example.sistemarecetas.Model.Clases.Medicamento;
import org.example.sistemarecetas.Model.Clases.Paciente;
import org.example.sistemarecetas.Model.Clases.Receta;
import org.example.sistemarecetas.Persistencia.Entity.RecetaEntity;

import java.time.LocalDate;

public class RecetaMapper {

    public static RecetaEntity toXml(Receta r) {
        if (r == null) return null;
        RecetaEntity x = new RecetaEntity();
        x.setId(r.getId());
        x.setEstado(r.getEstado());
        x.setCantidad(r.getCantidad());
        x.setDetalle(r.getDetalle());
        x.setMedicamento(r.getMedicamento() != null ? r.getMedicamento().getNombre() : "");
        x.setPaciente(r.getPaciente() != null ? r.getPaciente().getNombre() : "");
        x.setFechaConfeccion(r.getFechaConfeccion());
        x.setFechaEntrega(r.getFechaEntrega());
        return x;
    }

    public static Receta toModel(RecetaEntity x) {
        if (x == null) return null;

        Medicamento med = (x.getMedicamento() == null || x.getMedicamento().isBlank())
                ? null
                : new Medicamento("", x.getMedicamento(), ""); // código vacío, solo nombre

        Paciente pac = (x.getPaciente() == null || x.getPaciente().isBlank())
                ? null
                : new Paciente(0, "0", x.getPaciente(),
                x.getFechaConfeccion() != null ? x.getFechaConfeccion() : LocalDate.of(1990,1,1),
                ""); // id ficticio + nombre

        return new Receta(
                x.getId(),
                x.getEstado(),
                x.getCantidad(),
                x.getDetalle(),
                med,
                pac,
                x.getFechaConfeccion(),
                x.getFechaEntrega()
        );
    }
}