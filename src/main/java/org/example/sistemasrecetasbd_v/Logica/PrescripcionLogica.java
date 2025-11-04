package org.example.sistemasrecetasbd_v.Logica;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Model.Listas.HistoricoRecetas;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

public class PrescripcionLogica {

    public static void montarBusquedaPacientes(ObservableList<Paciente> observablePacientes, TableView<Paciente> tblPacientesPrescripcion,
                                               TextField txtBuscarPacientePrescripcion) {
        if (observablePacientes.isEmpty()) {
            tblPacientesPrescripcion.setItems(observablePacientes);
            return;
        }

        FilteredList<Paciente> filtered = new FilteredList<>(observablePacientes, p -> true);
        txtBuscarPacientePrescripcion.textProperty().addListener((obs, a, b) -> {
            String q = (b == null ? "" : b.trim().toLowerCase());
            filtered.setPredicate(p -> {
                if (q.isEmpty()) return true;
                String id = p.getIdentificacion().toLowerCase();
                String nombre = p.getNombre().toLowerCase();
                return id.contains(q) || nombre.contains(q);
            });
        });

        SortedList<Paciente> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tblPacientesPrescripcion.comparatorProperty());
        tblPacientesPrescripcion.setItems(sorted);
    }

    public static void montarBusquedaMedicamentos(ObservableList<Medicamento> observableMedicamentos, TableView<Medicamento> tblMedicamentosPrescripcion,
                                                  TextField txtBuscarMedicamentoPrescripcion) {
        if (observableMedicamentos.isEmpty()) {
            tblMedicamentosPrescripcion.setItems(observableMedicamentos);
            return;
        }

        FilteredList<Medicamento> filtered = new FilteredList<>(observableMedicamentos, m -> true);
        txtBuscarMedicamentoPrescripcion.textProperty().addListener((obs, a, b) -> {
            String q = (b == null ? "" : b.trim().toLowerCase(Locale.ROOT));
            filtered.setPredicate(m -> {
                if (q.isEmpty()) return true;
                String codigo = m.getCodigo().toLowerCase();
                String nombre = m.getNombre().toLowerCase(Locale.ROOT);
                return codigo.contains(q) || nombre.contains(q);
            });
        });

        SortedList<Medicamento> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tblMedicamentosPrescripcion.comparatorProperty());
        tblMedicamentosPrescripcion.setItems(sorted);
    }

    public static long recalcularDuracion(LocalDate fechaConfeccion, LocalDate fechaEntrega) {
        if (fechaConfeccion != null && fechaEntrega != null) {
            return ChronoUnit.DAYS.between(fechaConfeccion, fechaEntrega);
        }
        return 0;
    }

    public static void validarGuardarPrescripcion(HistoricoRecetas historicoRecetas, Paciente pacienteSeleccionado, Medicamento medicamentoSeleccionado,
                                                  LocalDate fechaConfeccion, LocalDate fechaEntrega, int cantidad, String detalle) {

        if (pacienteSeleccionado == null) {
            throw new IllegalArgumentException("Seleccione un paciente (doble click o selección en la tabla).");
        }
        if (medicamentoSeleccionado == null) {
            throw new IllegalArgumentException("Seleccione un medicamento (doble click o selección en la tabla).");
        }
        if (fechaConfeccion == null || fechaEntrega == null) {
            throw new IllegalArgumentException("Indique la fecha de confeccion y entrega");
        }
        if (fechaEntrega.isBefore(fechaConfeccion)) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la de confección.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero.");
        }
        if (detalle.isEmpty()) {
            throw new IllegalArgumentException("Debe haber un detalle.");
        }

        // Evita duplicados
        boolean duplicada = historicoRecetas.getItems().stream().anyMatch(r ->
                r != null && r.getPaciente() != null && r.getMedicamento() != null &&
                        "confeccionada".equalsIgnoreCase(r.getEstado()) &&
                        igualesPaciente(r.getPaciente(), pacienteSeleccionado) &&
                        igualesMedicamento(r.getMedicamento(), medicamentoSeleccionado) &&
                        Objects.equals(r.getFechaConfeccion(), fechaConfeccion)
        );

        if (duplicada) {
            throw new IllegalArgumentException("Ya existe una receta confeccionada hoy para ese paciente y medicamento.");
        }
    }

    public static Receta crearReceta(Paciente pacienteSeleccionado, Medicamento medicamentoSeleccionado, LocalDate fechaConfeccion,
                                     LocalDate fechaEntrega, int cantidad, String detalle) {
        return new Receta("confeccionada", cantidad, detalle, medicamentoSeleccionado,
                pacienteSeleccionado, fechaConfeccion, fechaEntrega);
    }

    private static boolean igualesPaciente(Paciente a, Paciente b) {
        try {
            String ai = String.valueOf(a.getIdentificacion());
            String bi = String.valueOf(b.getIdentificacion());
            if (ai != null && bi != null && !ai.equals("null") && !bi.equals("null")) {
                return ai.equals(bi);
            }
        } catch (Exception ignored) {}
        try {
            return Objects.equals(a.getId(), b.getId());
        } catch (Exception e) {
            return a == b;
        }
    }

    private static boolean igualesMedicamento(Medicamento a, Medicamento b) {
        try {
            return Objects.equals(a.getId(), b.getId());
        } catch (Exception e) {
            return a == b;
        }
    }
}
