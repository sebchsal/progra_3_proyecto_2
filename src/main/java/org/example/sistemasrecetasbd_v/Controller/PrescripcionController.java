package org.example.sistemasrecetasbd_v.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.example.sistemasrecetasbd_v.Logica.RecetaLogica;
import org.example.sistemasrecetasbd_v.Logica.PrescripcionLogica;
import org.example.sistemasrecetasbd_v.Model.Listas.HistoricoRecetas;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Model.Listas.CatalogoMedicamentos;
import org.example.sistemasrecetasbd_v.Model.Listas.ListaPacientes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private final RecetaLogica recetaLogica = new RecetaLogica();
    // Wrappers observables para las tablas
    private final ObservableList<Paciente> observablePacientes = FXCollections.observableArrayList();
    private final ObservableList<Medicamento> observableMedicamentos = FXCollections.observableArrayList();
    // Selecciones
    private Paciente pacienteSeleccionado;
    private Medicamento medicamentoSeleccionado;
    // formatea la fecha
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // se llama desde InicioController al abrir la ventana
    public void setListas(ListaPacientes lp, CatalogoMedicamentos cm, HistoricoRecetas hr, RecetaLogica rl) {
        this.listaPacientes = lp;
        this.catalogoMedicamentos = cm;
        this.historicoRecetas = hr;
        // Refrescar observables
        observablePacientes.setAll(this.listaPacientes.getItems());
        observableMedicamentos.setAll(this.catalogoMedicamentos.getItems());
        // Re-montar búsquedas y tablas
        montarBusquedaPacientes();
        montarBusquedaMedicamentos();
    }

    @FXML  public void initialize() {
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
        try {
            PrescripcionLogica.montarBusquedaPacientes(observablePacientes, tblPacientesPrescripcion, txtBuscarPacientePrescripcion);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    // búsqueda medicamentos
    private void montarBusquedaMedicamentos() {
        try {
            PrescripcionLogica.montarBusquedaMedicamentos(observableMedicamentos, tblMedicamentosPrescripcion, txtBuscarMedicamentoPrescripcion);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    //  calcula la duración con las fechas
    private void recalcularDuracion() {
        try {
            LocalDate confe = dtpFechaConfeccionPrescripcion.getValue();
            LocalDate entre = dtpFechaEntregaPrescripcion.getValue();
            long duracion = PrescripcionLogica.recalcularDuracion(confe, entre);
            txtduracionReceta.setText(String.valueOf(duracion));
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            txtduracionReceta.clear();
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
        // si el usuario no hizo doble click, toma la selección actual de la tabla
        if (pacienteSeleccionado == null) {
            pacienteSeleccionado = tblPacientesPrescripcion.getSelectionModel().getSelectedItem();
        }
        if (medicamentoSeleccionado == null) {
            medicamentoSeleccionado = tblMedicamentosPrescripcion.getSelectionModel().getSelectedItem();
        }

        try {
            // Validar selección efectiva y datos
            LocalDate confe = dtpFechaConfeccionPrescripcion.getValue();
            LocalDate entre = dtpFechaEntregaPrescripcion.getValue();
            int cantidad = spnCantidadPrescripcion.getValue() == null ? 1 : spnCantidadPrescripcion.getValue();
            String detalle = txtAreaDetallesPrescripcion.getText() == null ? "" : txtAreaDetallesPrescripcion.getText().trim();

            // Validar usando PrescripcionLogica
            PrescripcionLogica.validarGuardarPrescripcion(historicoRecetas, pacienteSeleccionado,
                    medicamentoSeleccionado, confe, entre, cantidad, detalle);

            // Crear receta usando PrescripcionLogica
            Receta nueva = PrescripcionLogica.crearReceta(pacienteSeleccionado, medicamentoSeleccionado,
                    confe, entre, cantidad, detalle);

            Async.run(() -> {
                try {
                    // Se ejecuta en segundo plano
                    return recetaLogica.insert(nueva);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, persistida -> {
                // Se ejecuta en el hilo de la UI
                if (persistida == null) {
                    mostrarAlerta("Error", "No se pudo guardar la receta en la base de datos.");
                    return;
                }
                historicoRecetas.agregarOReemplazar(persistida);
                spnCantidadPrescripcion.getValueFactory().setValue(1);
                txtAreaDetallesPrescripcion.clear();

                Stage stage = (Stage) txtduracionReceta.getScene().getWindow();
                stage.close();

                mostrarAlerta("Éxito", "Receta guardada correctamente.");
            }, ex -> {
                mostrarAlerta("Error al guardar la receta", ex.getMessage());
            });

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
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

