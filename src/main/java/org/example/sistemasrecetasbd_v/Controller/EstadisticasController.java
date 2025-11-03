package org.example.sistemasrecetasbd_v.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Logica.EstadisticasLogica;

public class EstadisticasController {
    @FXML private TextField txtBuscarDashboard;
    @FXML private TableView<Medicamento> tblMedicamentosDashboard;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamentoDashboard;
    @FXML private TableColumn<Medicamento, String> colMedicamentoDashboard;

    @FXML private BarChart<String, Number> graficoMedicamentos;
    @FXML private PieChart graficoRecetas;

    @FXML private Label lblCantidadRecetas;
    @FXML private Label lblRecetasConfeccion;
    @FXML private Label lblRecetasProceso;
    @FXML private Label lblRecetasListas;
    @FXML private Label lblRecetasEntregadas;

    private ObservableList<Medicamento> observableMedicamentosDashboard = FXCollections.observableArrayList();
    private TableView<Receta> historicoRecetas;
    private TableView<Medicamento> catalogoMedicamentos;
    private EstadisticasLogica logica = new EstadisticasLogica();

    public void inicializar(TableView<Receta> historicoRecetas, TableView<Medicamento> catalogoMedicamentos) {
        this.historicoRecetas = historicoRecetas;
        this.catalogoMedicamentos = catalogoMedicamentos;

        colCodigoMedicamentoDashboard.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCodigo()));
        colMedicamentoDashboard.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        observableMedicamentosDashboard.setAll(catalogoMedicamentos.getItems());
        tblMedicamentosDashboard.setItems(observableMedicamentosDashboard);
        tblMedicamentosDashboard.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Cargar estadísticas iniciales (Recetas y Medicamentos)
        cargarGraficoRecetas();
        cargarGraficoMedicamentos();
        // Actualizar al seleccionar medicamentos
        tblMedicamentosDashboard.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> cargarGraficoMedicamentos());
    }

    @FXML
    private void buscarDashboard() {
        try {
            String criterio = txtBuscarDashboard.getText().trim().toLowerCase();
            if (criterio.isEmpty()) {
                tblMedicamentosDashboard.setItems(observableMedicamentosDashboard);
                return;
            }
            ObservableList<Medicamento> filtrados = FXCollections.observableArrayList(
                    catalogoMedicamentos.getItems().stream()
                            .filter(m -> m.getNombre().toLowerCase().contains(criterio)
                                    || m.getCodigo().toLowerCase().contains(criterio))
                            .toList()
            );
            tblMedicamentosDashboard.setItems(filtrados);
        } catch (Exception e) {
            mostrarAlerta("Error al buscar medicamento", e.getMessage());
        }
    }

    @FXML
    private void cargarGraficoMedicamentos() {
        logica.cargarGraficoMedicamentos(historicoRecetas, tblMedicamentosDashboard, graficoMedicamentos);
    }

    @FXML
    private void cargarGraficoRecetas() {
        logica.cargarGraficoRecetas(historicoRecetas, graficoRecetas,
                lblCantidadRecetas, lblRecetasConfeccion, lblRecetasProceso, lblRecetasListas, lblRecetasEntregadas);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
