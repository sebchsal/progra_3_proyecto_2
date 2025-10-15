package org.example.sistemarecetas.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.example.sistemarecetas.Logica.Logica.RecetaLogica;
import org.example.sistemarecetas.Model.Listas.HistoricoRecetas;
import org.example.sistemarecetas.Model.Clases.Medicamento;
import org.example.sistemarecetas.Model.Clases.Paciente;
import org.example.sistemarecetas.Model.Clases.Receta;
import org.example.sistemarecetas.Model.Listas.CatalogoMedicamentos;
import org.example.sistemarecetas.Model.Listas.ListaPacientes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

public class PrescripcionController {

    @FXML private TextField txtBuscarPacientePrescripcion,
            txtBuscarMedicamentoPrescripcion, txtduracionReceta;
    @FXML private TextArea txtAreaDetallesPrescripcion;
    @FXML private Button btnVolverPrescripcion;
    @FXML private Spinner<Integer> spnCantidadPrescripcion;
    @FXML private DatePicker dtpFechaConfeccionPrescripcion, dtpFechaEntregaPrescripcion;
    @FXML private TableView<Paciente> tblPacientesPrescripcion;
    @FXML private TableColumn<Paciente, String> colIDPacientePrescripcion, colNombrePacientePrescripcion,
            colFechaPacientePrescripcion, colTelefonoPacientePrescripcion;
    @FXML private TableView<Medicamento> tblMedicamentosPrescripcion;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamentoPrescripcion,
            colNombreMedicamentoPrescripcion, colPresentacionMedicamentoPrescripcion;

    // Listas base, inyectadas desde InicioController
    private ListaPacientes listaPacientes;
    private CatalogoMedicamentos catalogoMedicamentos;
    private HistoricoRecetas historicoRecetas;
    private RecetaLogica recetaLogica;
    // Wrappers observables para las tablas
    private final ObservableList<Paciente> observablePacientes = FXCollections.observableArrayList();
    private final ObservableList<Medicamento> observableMedicamentos = FXCollections.observableArrayList();
    // Selecciones
    private Paciente pacienteSeleccionado;
    private Medicamento medicamentoSeleccionado;

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // se llama desde InicioController al abrir la ventana
    public void setListas(ListaPacientes lp, CatalogoMedicamentos cm, HistoricoRecetas hr, RecetaLogica rl) {
        this.listaPacientes = lp;
        this.catalogoMedicamentos = cm;
        this.historicoRecetas = hr;
        this.recetaLogica = rl;
        // Refrescar observables
        observablePacientes.setAll(this.listaPacientes.getItems());
        observableMedicamentos.setAll(this.catalogoMedicamentos.getItems());
        // Re-montar búsquedas y tablas
        montarBusquedaPacientes();
        montarBusquedaMedicamentos();
    }

    @FXML
    public void initialize() {
        // cantidad para el spinner del 1-999
        spnCantidadPrescripcion.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        // Fechas + duración
        txtduracionReceta.setEditable(false);
        dtpFechaConfeccionPrescripcion.setValue(LocalDate.now());
        dtpFechaEntregaPrescripcion.setValue(LocalDate.now());
        dtpFechaConfeccionPrescripcion.valueProperty().addListener((o,a,b) -> recalcularDuracion());
        dtpFechaEntregaPrescripcion.valueProperty().addListener((o,a,b) -> recalcularDuracion());
        recalcularDuracion();
        // Columnas Pacientes (usa los mismos getters que en InicioController)
        colIDPacientePrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getIdentificacion())));
        colNombrePacientePrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colFechaPacientePrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getFechaNacimiento() == null ? "" : cd.getValue().getFechaNacimiento().format(FECHA_FMT)));
        colTelefonoPacientePrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getTelefono() == null ? "" : cd.getValue().getTelefono()));
        // Columnas Medicamentos (igual que en InicioController)
        colCodigoMedicamentoPrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getCodigo())));
        colNombreMedicamentoPrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getNombre())));
        colPresentacionMedicamentoPrescripcion.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getTipoPresentacion())));
        // Enlazar tablas a sus observables
        tblPacientesPrescripcion.setItems(observablePacientes);
        tblMedicamentosPrescripcion.setItems(observableMedicamentos);
        // Búsqueda en vivo
        montarBusquedaPacientes();
        montarBusquedaMedicamentos();
        // Doble click para selección
        tblPacientesPrescripcion.setRowFactory(tv -> {
            TableRow<Paciente> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                    pacienteSeleccionado = row.getItem();
                    tblPacientesPrescripcion.getSelectionModel().select(pacienteSeleccionado);
                    info("Paciente seleccionado", "Paciente: " + pacienteSeleccionado.getNombre());
                }
            });
            return row;
        });
        tblMedicamentosPrescripcion.setRowFactory(tv -> {
            TableRow<Medicamento> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                    medicamentoSeleccionado = row.getItem();
                    tblMedicamentosPrescripcion.getSelectionModel().select(medicamentoSeleccionado);
                    info("Medicamento seleccionado", "Medicamento: " + medicamentoSeleccionado.getNombre());
                }
            });
            return row;
        });
    }

    // búsqueda pacientes
    private void montarBusquedaPacientes() {
        if (observablePacientes.isEmpty()) {
            tblPacientesPrescripcion.setItems(observablePacientes);
            return;
        }
        try{
            // filtra la lista
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
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }
    // búsqueda medicamentos
    private void montarBusquedaMedicamentos() {
        if (observableMedicamentos.isEmpty()) {
            tblMedicamentosPrescripcion.setItems(observableMedicamentos);
            return;
        }
        try{
            // filtra la lista
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
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }

    }
    //  calcula la duración con las fechas
    private void recalcularDuracion() {
        try{
            LocalDate confe = dtpFechaConfeccionPrescripcion.getValue();
            LocalDate entre = dtpFechaEntregaPrescripcion.getValue();
            if (confe != null && entre != null) {
                long dias = ChronoUnit.DAYS.between(confe, entre);
                txtduracionReceta.setText(String.valueOf(dias));
            } else {
                txtduracionReceta.clear();
            }
        }catch (Exception e){
            mostrarAlerta("Error", e.getMessage());
        }
    }

    // registra las recetas en la lista o tambien los pacientes modificados
    @FXML private void guardarPrescripcion() {
        if ((listaPacientes == null || listaPacientes.getItems().isEmpty()) &&
                (catalogoMedicamentos == null || catalogoMedicamentos.getItems().isEmpty())) {
            mostrarAlerta("Error en seleccion","No hay pacientes o medicamentos en las listas.");
            return;
        }
        if (historicoRecetas == null) {
            historicoRecetas = new HistoricoRecetas();
        }
        // Validar selección
        if (pacienteSeleccionado == null) {
            mostrarAlerta("Error en seleccion","Seleccione un paciente (doble click en la tabla).");
            return;
        }
        if (medicamentoSeleccionado == null) {
            mostrarAlerta("Error en seleccion","Seleccione un medicamento (doble click en la tabla).");
            return;
        }
        try{
            // Fechas
            LocalDate confe = dtpFechaConfeccionPrescripcion.getValue();
            LocalDate entre = dtpFechaEntregaPrescripcion.getValue();
            if (confe == null || entre == null) {
                mostrarAlerta("Error en fecha", "Indique la fecha de confeccion y entrega");
                return;
            }
            if (entre.isBefore(confe)) {
                mostrarAlerta("Error en fecha","La fecha de entrega no puede ser anterior a la de confección.");
                return;
            }
            int cantidad = spnCantidadPrescripcion.getValue() == null ? 1 : spnCantidadPrescripcion.getValue();
            String detalle = txtAreaDetallesPrescripcion.getText() == null ? "" : txtAreaDetallesPrescripcion.getText().trim();
            if (cantidad <= 0 && detalle.isEmpty()) {
                mostrarAlerta("Error", "Cantidad debe ser mayor a cero y debe haber un detelle");
                return; }

            // Evitar duplicados
            boolean duplicada = historicoRecetas.getItems().stream().anyMatch(r ->
                    r != null && r.getPaciente() != null && r.getMedicamento() != null &&
                            "confeccionada".equalsIgnoreCase(r.getEstado()) &&
                            igualesPaciente(r.getPaciente(), pacienteSeleccionado) &&
                            igualesMedicamento(r.getMedicamento(), medicamentoSeleccionado) &&
                            Objects.equals(r.getFechaConfeccion(), confe)
            );
            if (duplicada) {
                mostrarAlerta("Error", "Ya existe una receta confeccionada hoy para ese paciente y medicamento.");
                return;
            }
            Receta nueva = new Receta("confeccionada", cantidad, detalle, medicamentoSeleccionado, pacienteSeleccionado, confe, entre);
            historicoRecetas.agregarOReemplazar(nueva);
            recetaLogica.create(nueva);
            spnCantidadPrescripcion.getValueFactory().setValue(1);
            txtAreaDetallesPrescripcion.clear();
            Stage stage = (Stage) txtduracionReceta.getScene().getWindow();
            stage.close();
        }catch (Exception e){
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private static boolean igualesPaciente(Paciente a, Paciente b) {
        try {
            // compara por identificacion si existe, si no por id
            String ai = String.valueOf(a.getIdentificacion());
            String bi = String.valueOf(b.getIdentificacion());
            if (ai != null && bi != null && !ai.equals("null") && !bi.equals("null")) {
                return ai.equals(bi);
            }
        } catch (Exception ignored) {}
        try { return Objects.equals(a.getId(), b.getId()); } catch (Exception e) { return a == b; }
    }

    private static boolean igualesMedicamento(Medicamento a, Medicamento b) {
        try { return Objects.equals(a.getId(), b.getId()); }
        catch (Exception e) { return a == b; }
    }

    @FXML private void volver() {
        Stage stage = (Stage) btnVolverPrescripcion.getScene().getWindow();
        stage.setUserData(null);
        stage.close();
    }

    private static void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

