package org.example.sistemasrecetasbd_v.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.sistemasrecetasbd_v.Logica.*;
import org.example.sistemasrecetasbd_v.Model.Clases.*;
import org.example.sistemasrecetasbd_v.Model.Listas.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

public class InicioController implements Initializable {
    // boton de cerrarsesion y label que indica el usuario...
    @FXML private Button btncerrarsesion;
    @FXML private Label lblusuarioactual;

    // progressindicators
    @FXML private ProgressIndicator progMedtab;
    @FXML private ProgressIndicator progFarmtab;
    @FXML private ProgressIndicator progPactab;
    @FXML private ProgressIndicator progMedctab;
    @FXML private ProgressIndicator progRecetatab;

    // Para la ocultacion de los tab segun usuarios...
    @FXML private TabPane tabPane;
    @FXML private Tab tabMedicos, tabFarmaceutas, tabPacientes, tabMedicamentos;

    // Tabla de Medicos...
    @FXML private TableView<Medico> tblListaMedicos;
    @FXML private TableColumn<Medico, String> colIDMedicos, colIdentificacionMedicos,
            colNombreMedicos, colEspecialidadMedicos;
    @FXML private TextField txtBuscarMedicos;
    // Datos de Medicos
    private ListaMedicos listaMedicos;
    private final ObservableList<Medico> observableMedicos = FXCollections.observableArrayList();
    // Datos para tabla de Medicos en BD
    private final MedicoLogica medicoLogica = new MedicoLogica();

    // Tabla de Farmaceutas
    @FXML private TableView<Farmaceuta> tblListaFarmaceutas;
    @FXML private TableColumn<Farmaceuta, String> colIDFarmaceutas, colIdentificacionFarmaceutas, colNombreFarmaceutas;
    @FXML private TextField txtBuscarFarmaceutas;
    // Datos de Farmaceutas...
    private ListaFarmaceutas listaFarmaceutas;
    private ObservableList<Farmaceuta> observableFarmaceutas = FXCollections.observableArrayList();
    // Datos para tabla de Farmaceutas en BD
    private final FarmaceutaLogica farmaceutaLogica = new FarmaceutaLogica();

    // Tabla de pacientes
    @FXML private TableView<Paciente> tblListaPacientes;
    @FXML private TableColumn<Paciente, String> colIdentificacionPaciente, colIDPacientes,
            colNombrePacientes, colFechaNacimientoPacientes, colTelefonoPaciente;
    @FXML private TextField txtBuscarPacientes;
    // Datos de pacientes
    private ListaPacientes listaPacientes;
    private final ObservableList<Paciente> observablePacientes = FXCollections.observableArrayList();
    // Datos para tabla de Pacientes en BD
    private final PacienteLogica pacienteLogica = new PacienteLogica();

    // Table de Medicamentos
    @FXML private TableView<Medicamento> tblListaMedicamentos;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamentos, colNombreMedicamentos, colPresentacionMedicamentos;
    @FXML private TextField txtBuscarMedicamentos;
    @FXML private Button btnPrescripcion, btnDespacho;
    private CatalogoMedicamentos catalogoMedicamentos;
    private final ObservableList<Medicamento> observableMedicamentos = FXCollections.observableArrayList();
    // Datos para tabla de Medicamento en BD
    private final MedicamentoLogica medicamentoLogica = new MedicamentoLogica();

    // Table de Recetas
    private HistoricoRecetas historicoRecetas;
    private final ObservableList<Receta> observableHistoricoRecetas = FXCollections.observableArrayList();
    @FXML private TableView<Receta> tblListaHistorial;
    @FXML private TableColumn<Receta, String> colCodigoHistorial, colMedicamentoHistorial,
            colPacienteHistorial, colEstadoHistorial, colcantHistorial;
    // Datos para tabla de Receta en BD
    private final RecetaLogica recetaLogica = new RecetaLogica();

    // Dashboard
    // Tabla de busqueda de medicamentos en dashboard
    @FXML private TableView<Medicamento> tblMedicamentosDashboard;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamentoDashboard;
    @FXML private TableColumn<Medicamento, String> colMedicamentoDashboard;

    @FXML private TextField txtBuscarDashboard;

    // Barchart de medicamentos
    @FXML private BarChart<String, Number> graficoMedicamentos;

    private final ObservableList<Medicamento> observableMedicamentosDashboard = FXCollections.observableArrayList();

    // Piechart de recetas
    @FXML private PieChart graficoRecetas;
    @FXML private Label lblCantidadRecetas;
    @FXML private Label lblRecetasConfeccion;
    @FXML private Label lblRecetasProceso;
    @FXML private Label lblRecetasListas;
    @FXML private Label lblRecetasEntregadas;

    // User information
    private String userType = "";
    private Object currentUser = null;

    //Para formatear la fecha de nacimiento
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setListas(ListaMedicos lm, ListaFarmaceutas lf, ListaPacientes lp, CatalogoMedicamentos cm, HistoricoRecetas hr) throws SQLException {
        this.listaMedicos = lm;
        this.listaFarmaceutas = lf;
        this.listaPacientes = lp;
        this.catalogoMedicamentos = cm;
        this.historicoRecetas = hr;
    }

    public void setUserType(String userType, Object user) {
        this.userType = userType;
        this.currentUser = user;
        setupPermissions();
        updateUserDisplay();
        setupTabVisibility();
    }

    // inicializador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            // Configurar columnas de medicos
            colIDMedicos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getId())));
            colIdentificacionMedicos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getIdentificacion())));
            colNombreMedicos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
            colEspecialidadMedicos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getEspecialidad()));

            // Configurar las columnas de farmaceutas
            colIDFarmaceutas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getId())));
            colIdentificacionFarmaceutas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getIdentificacion())));
            colNombreFarmaceutas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
            // Cargar farmaceutas desde XML

            // Configurar las columnas de pacientes
            colIDPacientes.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getId())));
            colIdentificacionPaciente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getIdentificacion()));
            colNombrePacientes.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getNombre())));
            colFechaNacimientoPacientes.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getFechaNacimiento() == null ? "" : cd.getValue().getFechaNacimiento().format(FECHA_FMT)));
            colTelefonoPaciente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTelefono() == null ? "" : cd.getValue().getTelefono()));

            // Configurar las columnas de medicamentos
            colCodigoMedicamentos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getCodigo())));
            colNombreMedicamentos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getNombre())));
            colPresentacionMedicamentos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getTipoPresentacion())));

            // Configurar las columnas de historial
            colCodigoHistorial.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getId())));
            colMedicamentoHistorial.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getMedicamento().getNombre())));
            colPacienteHistorial.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getPaciente().getNombre())));
            colEstadoHistorial.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getEstado())));
            colcantHistorial.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getCantidad())));

            // Dashboard
            // Configura columnas de medicamentos en el dashboard
            colCodigoMedicamentoDashboard.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getCodigo()));
            colMedicamentoDashboard.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));

            tblMedicamentosDashboard.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tblMedicamentosDashboard.getSelectionModel().getSelectedItems()
                    .addListener((ListChangeListener<Medicamento>) c -> cargarGraficoMedicamentos());

            // Ligar tabla con observable list
            tblListaMedicos.setItems(observableMedicos);
            cargarMedicosAsync();
            tblListaFarmaceutas.setItems(observableFarmaceutas);
            cargarFarmaceutasAsync();
            tblListaPacientes.setItems(observablePacientes);
            cargarPacienteAsync();
            tblListaMedicamentos.setItems(observableMedicamentos);
            cargarMedicamentoAsync();
            tblListaHistorial.setItems(observableHistoricoRecetas);
            cargarRecetaAsync();
            tblMedicamentosDashboard.setItems(observableMedicamentosDashboard);
        }catch (Exception e) {
            Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void setupPermissions() {
        boolean isMedico = "medico".equals(userType);
        boolean isFarmaceuta = "farmacéuta".equals(userType);

        // se desabilita el boton preescripcion si no es medico
        btnPrescripcion.setDisable(!isMedico);
        // se desabilita el boton despacho si no es farmaceutico
        btnDespacho.setDisable(!isFarmaceuta);

        // Actualiza el estilo de los botones
        updateButtonStyles();
    }

    private void setupTabVisibility() {
        // Esconde al inicio los tabs
        tabMedicos.setDisable(true);
        tabFarmaceutas.setDisable(true);
        tabPacientes.setDisable(true);
        tabMedicamentos.setDisable(true);
        switch (userType) {
            case "admin":
                tabMedicos.setDisable(false);
                tabFarmaceutas.setDisable(false);
                tabPacientes.setDisable(false);
                tabPane.getSelectionModel().select(0);
                break;
            case "medico", "farmacéuta":
                tabMedicamentos.setDisable(false);
                tabPane.getSelectionModel().select(0);
                break;
        }
    }

    private void updateUserDisplay() {
        if (lblusuarioactual != null && currentUser != null) {
            String userName = "";
            switch (currentUser) {
                case Administrador administrador -> userName = administrador.getNombre();
                case Medico medico -> userName = medico.getNombre();
                case Farmaceuta farmaceuta -> userName = farmaceuta.getNombre();
                default -> {
                }
            }

            String userRole = switch (userType) {
                case "admin" -> "Administrador";
                case "medico" -> "Médico";
                case "farmacéuta" -> "Farmacéuta";
                default -> "";
            };

            lblusuarioactual.setText(userName + " (" + userRole + ")");
        }
    }

    // Edita el color de los botones
    private void updateButtonStyles() {
        String disabledStyle = "-fx-opacity: 0.5;";
        String enabledStyle = "-fx-opacity: 1.0;";

        // edita boton preescripcion
        btnPrescripcion.setStyle(btnPrescripcion.isDisabled() ? disabledStyle : enabledStyle);
        // edita boton despacho
        btnDespacho.setStyle(btnDespacho.isDisabled() ? disabledStyle : enabledStyle);
    }

    // ---------- Metodos de tab medicos ----------
    // boton agregar
    @FXML private void agregarMedico() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden agregar médicos.");
            return;
        }
        Medico nuevo = mostrarFormularioMedico(null, false);
        if (nuevo != null) {
            if (listaMedicos.buscarPorIdentificacion(nuevo.getIdentificacion()).isPresent()) {
                mostrarAlerta("Duplicado", "Ya existe un médico con esa identificación.");
                return;
            }
            try {
                Medico creado = medicoLogica.insert(nuevo);
                listaMedicos.agregarOReemplazar(creado);
                observableMedicos.add(nuevo);
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        }
    }

    // boton modificar
    @FXML private void modificarMedico() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden modificar médicos.");
            return;
        }
        Medico seleccionado = tblListaMedicos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un médico", "Por favor seleccione un médico de la tabla.");
            return;
        }

        Medico modificado = mostrarFormularioMedico(seleccionado, true);
        if (modificado != null) {
            try {
                medicoLogica.update(modificado);
                listaMedicos.agregarOReemplazar(modificado);
                tblListaMedicos.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al actualizar", e.getMessage());
            }
        }
    }

    // boton eliminar
    @FXML private void eliminarMedico() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden eliminar médicos.");
            return;
        }
        Medico seleccionado = tblListaMedicos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un médico", "Seleccione un médico para eliminar.");
            return;
        }

        try {
            boolean ok = medicoLogica.delete(seleccionado.getId());
            if (ok) {
                listaMedicos.eliminarPorId(seleccionado.getId());
                observableMedicos.remove(seleccionado);
            }
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar", e.getMessage());
        }
    }

    // boton buscar
    @FXML private void buscarMedico() {
        try {
            String criterio = txtBuscarMedicos.getText().trim().toLowerCase();
            if (criterio.isEmpty()) {
                tblListaMedicos.setItems(observableMedicos);
                return;
            }

            tblListaMedicos.setItems(FXCollections.observableArrayList(
                    listaMedicos.getItems().stream()
                            .filter(m -> m.getIdentificacion().toLowerCase().contains(criterio) ||
                                    m.getNombre().toLowerCase().contains(criterio) ||
                                    m.getEspecialidad().toLowerCase().contains(criterio)).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            mostrarAlerta("Error al buscar el cliente", e.getMessage());
        }
    }

    // mostrar el formulario de agregarMedico.fxml para los botones agregar y modificar en tab medicos
    private Medico mostrarFormularioMedico(Medico medico, boolean editar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/AgregarMedicos.fxml"));
            Parent root = loader.load();

            AgregarMedicosController controller = loader.getController();
            controller.setMedico(medico, editar);

            Stage stage = new Stage();
            stage.setTitle(editar ? "Modificar Médico" : "Agregar Médico");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return (Medico) stage.getUserData();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario", e.getMessage());
            return null;
        }
    }

    // ---------- Metodos de tab farmaceutas ----------
    // boton agregar
    @FXML private void agregarFarmaceuta() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden agregar farmaceutas.");
            return;
        }
        Farmaceuta nuevo = mostrarFormularioFarmaceuta(null, false);
        if (nuevo != null) {
            if (listaFarmaceutas.buscarPorIdentificacion(nuevo.getIdentificacion()).isPresent()) {
                mostrarAlerta("Duplicado", "Ya existe un farmaceuta con esa identificación.");
                return;
            }
            try {
                Farmaceuta creado = farmaceutaLogica.insert(nuevo);
                listaFarmaceutas.agregarOReemplazar(creado);
                observableFarmaceutas.add(creado);
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        }
    }

    // boton modificar
    @FXML private void modificarFarmaceuta() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden modificar farmaceutas.");
            return;
        }
        Farmaceuta seleccionado = tblListaFarmaceutas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un farmaceuta", "Seleccione un farmaceuta de la tabla para modificar.");
            return;
        }

        Farmaceuta modificado = mostrarFormularioFarmaceuta(seleccionado, true);
        if (modificado != null) {
            try {
                farmaceutaLogica.update(modificado);
                listaFarmaceutas.agregarOReemplazar(modificado);
                tblListaFarmaceutas.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al actualizar", e.getMessage());
            }
        }
    }

    // boton eliminar
    @FXML private void eliminarFarmaceuta() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden eliminar farmaceutas.");
            return;
        }
        try {
            Farmaceuta seleccionado = tblListaFarmaceutas.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Seleccione un farmaceuta", "Seleccione un farmaceuta de la tabla para eliminar.");
                return;
            }
            boolean eliminado = farmaceutaLogica.delete(seleccionado.getId());
            if (!eliminado) {
                mostrarAlerta("Error", "No se pudo eliminar el farmaceuta de la base de datos.");
                return;
            }
            listaFarmaceutas.eliminarPorId(seleccionado.getId());
            tblListaFarmaceutas.getItems().remove(seleccionado);
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar farmaceuta", e.getMessage());
        }
    }

    // boton buscar
    @FXML private void buscarFarmaceuta() {
        String criterio = txtBuscarFarmaceutas.getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            tblListaFarmaceutas.setItems(observableFarmaceutas);
            return;
        }

        tblListaFarmaceutas.setItems(FXCollections.observableArrayList(
                listaFarmaceutas.getItems().stream().filter(f -> f.getNombre().toLowerCase().contains(criterio) ||
                        f.getIdentificacion().toLowerCase().contains(criterio)).collect(Collectors.toList())
        ));
    }

    // metodo para mostrar el formulario de agregarFarmaceuta en los botones de agregar y modificar en tab farmaceutas
    private Farmaceuta mostrarFormularioFarmaceuta(Farmaceuta farmaceuta, boolean editar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/AgregarFarmaceutas.fxml"));
            Parent root = loader.load();

            AgregarFarmaceutasController ctrl = loader.getController();
            ctrl.setFarmaceuta(farmaceuta, editar);

            Stage stage = new Stage();
            stage.setTitle(editar ? "Modificar Farmaceuta" : "Agregar Farmaceuta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return (Farmaceuta) stage.getUserData();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario", e.getMessage());
            return null;
        }
    }

    // ---------- Metodos de tab pacientes ----------
    // boton agregar
    @FXML private void agregarPaciente() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden agregar pacientes.");
            return;
        }
        Paciente nuevo = mostrarFormularioPaciente(null, false);
        if (nuevo != null) {
            if (listaPacientes.buscarPorIdentificacionPaciente(nuevo.getIdentificacion()).isPresent()) {
                mostrarAlerta("Duplicado", "Ya existe un médico con esa identificación.");
                return;
            }
            try {
                Paciente creado = pacienteLogica.insert(nuevo);
                listaPacientes.agregarOReemplazar(creado);
                observablePacientes.add(creado);
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        }
    }

    // boton modificar
    @FXML private void modificarPaciente() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden modificar pacientes.");
            return;
        }
        Paciente seleccionado = tblListaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un paciente", "Seleccione un paciente de la tabla para modificar.");
            return;
        }

        Paciente modificado = mostrarFormularioPaciente(seleccionado, true);
        if (modificado != null) {
            try {
                pacienteLogica.update(modificado);
                listaPacientes.agregarOReemplazar(modificado);
                tblListaPacientes.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al actualizar", e.getMessage());
            }
        }
    }

    // boton eliminar
    @FXML private void eliminarPaciente() {
        if (!"admin".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los administradores pueden eliminar pacientes.");
            return;
        }
        try {
            Paciente seleccionado = tblListaPacientes.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Seleccione un paciente", "Seleccione un paciente de la tabla para eliminar.");
                return;
            }

            boolean eliminado = pacienteLogica.delete(seleccionado.getId());
            if (!eliminado) {
                mostrarAlerta("Error", "No se pudo eliminar el paciente de la base de datos.");
                return;
            }

            listaPacientes.eliminarPorId(seleccionado.getId());
            tblListaPacientes.getItems().remove(seleccionado);
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar paciente", e.getMessage());
        }
    }

    // boton buscar
    @FXML private void buscarPaciente() {
        String criterio = txtBuscarPacientes.getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            tblListaPacientes.setItems(observablePacientes);
            return;
        }
        tblListaPacientes.setItems(FXCollections.observableArrayList(
                listaPacientes.getItems().stream().filter(p -> p.getNombre().toLowerCase().contains(criterio) ||
                        p.getIdentificacion().toLowerCase().contains(criterio) ||
                        p.getTelefono().toLowerCase().contains(criterio)).collect(Collectors.toList())
        ));
    }

    // metodo para mostrar el formulario de agregarPaciente en los botones de agregar y modificar en tab pacientes
    private Paciente mostrarFormularioPaciente(Paciente paciente, boolean editar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/AgregarPaciente.fxml"));
            Parent root = loader.load();

            AgregarPacienteController controller = loader.getController();
            controller.setPaciente(paciente, editar);

            Stage stage = new Stage();
            stage.setTitle(editar ? "Modificar Paciente" : "Agregar Paciente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return (Paciente) stage.getUserData();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario", e.getMessage());
            return null;
        }
    }

    // ---------- Metodos de tab Medicamentos ----------
    // boton agregar
    @FXML private void agregarMedicamento() {
        if (!"medico".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los medicos pueden agregar medicamento.");
            return;
        }
        Medicamento nuevo = mostrarFormularioMedicamentos(null, false);
        if (nuevo != null) {
            if (catalogoMedicamentos.buscarPorCodigo(nuevo.getCodigo()).isPresent()) {
                mostrarAlerta("Duplicado", "Ya existe un médico con esa identificación.");
                return;
            }
            try {
                Medicamento creado = medicamentoLogica.insert(nuevo);
                catalogoMedicamentos.agregarOReemplazar(creado);
                observableMedicamentos.add(creado);
                observableMedicamentosDashboard.add(creado);
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        }
    }

    // boton modificar
    @FXML private void modificarMedicamento() {
        if (!"medico".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los medicos pueden modificar pacientes.");
            return;
        }
        Medicamento seleccionado = tblListaMedicamentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un medicamento", "Seleccione un medicamento de la tabla para modificar.");
            return;
        }

        Medicamento modificado = mostrarFormularioMedicamentos(seleccionado, true);
        if (modificado != null) {
            try {
                medicamentoLogica.update(modificado);
                catalogoMedicamentos.agregarOReemplazar(modificado);
                tblListaMedicamentos.refresh();
                tblMedicamentosDashboard.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al actualizar", e.getMessage());
            }
        }
    }

    // boton eliminar
    @FXML private void eliminarMedicamento() {
        if (!"medico".equals(userType)) {
            mostrarAlerta("Permiso denegado", "Solo los medicos pueden eliminar pacientes.");
            return;
        }
        try {
            Medicamento seleccionado = tblListaMedicamentos.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Seleccione un medicamento", "Seleccione un medicamento de la tabla para eliminar.");
                return;
            }

            boolean eliminado = medicamentoLogica.delete(seleccionado.getId());
            if (!eliminado) {
                mostrarAlerta("Error", "No se pudo eliminar el medicamento de la base de datos.");
                return;
            }

            catalogoMedicamentos.eliminarPorId(seleccionado.getId());
            tblListaMedicamentos.getItems().remove(seleccionado);
            observableMedicamentosDashboard.remove(seleccionado);
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar medicamento", e.getMessage());
        }
    }

    // boton buscar
    @FXML private void buscarMedicamento() {
        String criterio = txtBuscarMedicamentos.getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            tblListaMedicamentos.setItems(observableMedicamentos);
            return;
        }

        tblListaMedicamentos.setItems(FXCollections.observableArrayList(
                catalogoMedicamentos.getItems().stream().filter(m -> m.getNombre().toLowerCase().contains(criterio) ||
                        m.getCodigo().toLowerCase().contains(criterio)).collect(Collectors.toList())
        ));
    }

    // metodo para mostrar el formulario de agregarMedicamento en los botones de agregar y modificar en tab medicamentos
    private Medicamento mostrarFormularioMedicamentos(Medicamento medicamento, boolean editar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/AgregarMedicamento.fxml"));
            Parent root = loader.load();

            AgregarMedicamentoController ctrl = loader.getController();
            ctrl.setMedicamento(medicamento, editar);

            Stage stage = new Stage();
            stage.setTitle(editar ? "Modificar Medicamento" : "Agregar Maciente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return (Medicamento) stage.getUserData();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario", e.getMessage());
            return null;
        }
    }

    // metodo para abrir preescripcion
    @FXML private void abrirPrescripcion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/Prescripcion.fxml"));
            Parent root = loader.load();

            PrescripcionController ctrl = loader.getController();
            ctrl.setListas(listaPacientes, catalogoMedicamentos, historicoRecetas, recetaLogica);

            Stage stage = new Stage();
            stage.setTitle("Prescripción");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            observableHistoricoRecetas.setAll(historicoRecetas.getItems());
            cargarGraficoRecetas();
            cargarGraficoMedicamentos();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir prescripcion", e.getMessage());
        }
    }

    // metodo para abrir despacho
    @FXML private void abrirDespacho() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/Despacho.fxml"));
            Parent root = loader.load();
            DespachoController ctrl = loader.getController();
            // Pasar referencia al historico como en abrirPrescripcion
            ctrl.setListas(historicoRecetas, recetaLogica);
            Stage stage = new Stage();
            stage.setTitle("Despacho de Recetas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Al cerrar, refrescamos el historial
            if (historicoRecetas != null) {
                observableHistoricoRecetas.setAll(historicoRecetas.getItems());
                tblListaHistorial.refresh();
                cargarGraficoRecetas();
                cargarGraficoMedicamentos();
            }
        } catch (Exception e) {
            mostrarAlerta("Error al abrir despacho", e.getMessage());
        }
    }
    // metodo para abrir detalles de la receta
    @FXML private void mostrarDetallesReceta() {
        Receta seleccionada = tblListaHistorial.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Seleccione una receta", "Por favor seleccione una receta para ver los detalles.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/DetalleReceta.fxml"));
            Parent root = loader.load();

            DetalleController controller = loader.getController();
            controller.setReceta(seleccionada);

            Stage stage = new Stage();
            stage.setTitle("Detalles de Receta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error al abrir detalles", e.getMessage());
        }
    }
    // Metodos de tab Dashboard
    @FXML private void buscarDashboard() {
        try {
            String criterio = txtBuscarDashboard.getText().trim().toLowerCase();
            if (criterio.isEmpty()) {
                tblMedicamentosDashboard.setItems(observableMedicamentosDashboard);
                return;
            }
            ObservableList<Medicamento> filtrados = FXCollections.observableArrayList(
                    catalogoMedicamentos.getItems().stream()
                            .filter(m -> m.getNombre().toLowerCase().contains(criterio) ||
                                    m.getCodigo().toLowerCase().contains(criterio)).toList()
            );

            tblMedicamentosDashboard.setItems(filtrados);
        } catch (Exception e) {
            mostrarAlerta("Error al buscar medicamento", e.getMessage());
        }
    }

    private void cargarGraficoMedicamentos() {
        if (historicoRecetas == null || historicoRecetas.getItems().isEmpty()) {
            graficoMedicamentos.getData().clear();
            return;
        }
        var seleccionados = tblMedicamentosDashboard.getSelectionModel().getSelectedItems();
        if (seleccionados == null || seleccionados.isEmpty()) {
            graficoMedicamentos.getData().clear();
            return;
        }
        graficoMedicamentos.getData().clear();

        for (Medicamento med : seleccionados) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(med.getNombre());
            var mesesUsados = historicoRecetas.getItems().stream()
                    .filter(r -> r.getMedicamento() != null && r.getMedicamento().getNombre() != null)
                    .filter(r -> med.getNombre().equalsIgnoreCase(r.getMedicamento().getNombre()))
                    .filter(r -> r.getFechaConfeccion() != null)
                    .map(r -> r.getFechaConfeccion().getYear() + "-" +
                            String.format("%02d", r.getFechaConfeccion().getMonthValue())).distinct().sorted().toList();

            for (String mes : mesesUsados) {
                long cantidad = historicoRecetas.getItems().stream()
                        .filter(r -> r.getMedicamento() != null && r.getMedicamento().getNombre() != null)
                        .filter(r -> med.getNombre().equalsIgnoreCase(r.getMedicamento().getNombre()))
                        .filter(r -> {
                            if (r.getFechaConfeccion() == null) return false;
                            String ym = r.getFechaConfeccion().getYear() + "-" +
                                    String.format("%02d", r.getFechaConfeccion().getMonthValue());
                            return ym.equals(mes);
                        })
                        .mapToLong(Receta::getCantidad).sum();

                serie.getData().add(new XYChart.Data<>(mes, cantidad));
            }
            graficoMedicamentos.getData().add(serie);
        }
    }

    private void cargarGraficoRecetas() {
        if (historicoRecetas == null) return;

        long confeccionadas = historicoRecetas.getItems().stream()
                .filter(r -> "confeccionada".equalsIgnoreCase(r.getEstado())).count();
        long proceso = historicoRecetas.getItems().stream()
                .filter(r -> "proceso".equalsIgnoreCase(r.getEstado())).count();
        long listas = historicoRecetas.getItems().stream()
                .filter(r -> "lista".equalsIgnoreCase(r.getEstado())).count();
        long entregadas = historicoRecetas.getItems().stream()
                .filter(r -> "entregada".equalsIgnoreCase(r.getEstado())).count();

        long total = confeccionadas + proceso + listas + entregadas;

        // Actualizar labels de resumen
        lblCantidadRecetas.setText(String.valueOf(total));
        lblRecetasConfeccion.setText(String.valueOf(confeccionadas));
        lblRecetasProceso.setText(String.valueOf(proceso));
        lblRecetasListas.setText(String.valueOf(listas));
        lblRecetasEntregadas.setText(String.valueOf(entregadas));
        graficoRecetas.getData().clear();

        if (confeccionadas > 0) {
            double porcentaje = (confeccionadas * 100.0) / total;
            PieChart.Data d1 = new PieChart.Data(String.format("Confeccionada (%.1f%%)", porcentaje), confeccionadas);
            graficoRecetas.getData().add(d1);
            d1.getNode().setStyle("-fx-pie-color: #3498db;");
        }
        if (proceso > 0) {
            double porcentaje = (proceso * 100.0) / total;
            PieChart.Data d2 = new PieChart.Data(String.format("Proceso (%.1f%%)", porcentaje), proceso);
            graficoRecetas.getData().add(d2);
            d2.getNode().setStyle("-fx-pie-color: #e74c3c;");
        }
        if (listas > 0) {
            double porcentaje = (listas * 100.0) / total;
            PieChart.Data d3 = new PieChart.Data(String.format("Lista (%.1f%%)", porcentaje), listas);
            graficoRecetas.getData().add(d3);
            d3.getNode().setStyle("-fx-pie-color: #f1c40f;");
        }
        if (entregadas > 0) {
            double porcentaje = (entregadas * 100.0) / total;
            PieChart.Data d4 = new PieChart.Data(String.format("Entregada (%.1f%%)", porcentaje), entregadas);
            graficoRecetas.getData().add(d4);
            d4.getNode().setStyle("-fx-pie-color: #2ecc71;");
        }
    }

    // metodo cerrar sesion...
    @FXML private void cerrarSesion() {
        try {
            LoginController.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/Login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setListas(listaMedicos, listaFarmaceutas, listaPacientes, catalogoMedicamentos, historicoRecetas);
            Stage stage = (Stage) btncerrarsesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión");
        } catch (Exception e) {
            mostrarAlerta("Error al cerrar sesion", e.getMessage());
        }
    }

    public void cargarMedicosAsync(){
        progMedtab.setVisible(true);
        Async.run(() -> {
            try {
                return medicoLogica.findAll();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, lista -> {
            listaMedicos.setAll(lista);
            observableMedicos.setAll(listaMedicos.getItems());
        }, ex -> mostrarAlerta("Error al cargar médicos", ex.getMessage()));
    }

    public void cargarFarmaceutasAsync(){
        progFarmtab.setVisible(true);
        Async.run(() -> {
            try {
                return farmaceutaLogica.findAll();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, lista -> {
            listaFarmaceutas.setAll(lista);
            observableFarmaceutas.setAll(listaFarmaceutas.getItems());
        }, ex -> mostrarAlerta("Error al cargar farmaceutas", ex.getMessage()));
    }

    public void cargarPacienteAsync(){
        progPactab.setVisible(true);
        Async.run(() -> {
            try {
                return pacienteLogica.findAll();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, lista -> {
            listaPacientes.setAll(lista);
            observablePacientes.setAll(listaPacientes.getItems());
        }, ex -> mostrarAlerta("Error al cargar pacientes", ex.getMessage()));
    }

    public void cargarMedicamentoAsync(){
        progMedctab.setVisible(true);
        Async.run(() -> {
            try {
                return medicamentoLogica.findAll();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, lista -> {
            catalogoMedicamentos.setAll(lista);
            observableMedicamentos.setAll(catalogoMedicamentos.getItems());
            observableMedicamentosDashboard.setAll(catalogoMedicamentos.getItems());
        }, ex -> mostrarAlerta("Error al cargar medicamentos", ex.getMessage()));
    }

    public void cargarRecetaAsync(){
        progRecetatab.setVisible(true);
        Async.run(() -> {
            try {
                return recetaLogica.findAll();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, lista -> {
            historicoRecetas.setAll(lista);
            observableHistoricoRecetas.setAll(historicoRecetas.getItems());
            cargarGraficoRecetas();
            cargarGraficoMedicamentos();
        }, ex -> mostrarAlerta("Error al cargar recetas", ex.getMessage()));
    }

    // muestra las alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}