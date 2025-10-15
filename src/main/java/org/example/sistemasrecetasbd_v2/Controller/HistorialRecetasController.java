package org.example.sistemarecetas.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.example.sistemarecetas.Model.Listas.HistoricoRecetas;
import org.example.sistemarecetas.Model.Clases.Receta;

public class HistorialRecetasController {

    @FXML private TableView<Receta> tbvDespacho;
    @FXML private TableColumn<Receta, String> tcIdDespacho;
    @FXML private TableColumn<Receta, String> tcMedicamentoDespacho;
    @FXML private TableColumn<Receta, String> tcPacienteDespacho;
    @FXML private TableColumn<Receta, String> tcEstadoDespacho;
    @FXML private TableColumn<Receta, String> tcCantDespacho;
    @FXML private TextField txtHistorialDespacho;
    @FXML private Button btnBuscarRecetasDespacho;
    @FXML private Button btnSalirRecetasDespacho;

    private final ObservableList<Receta> respaldo = FXCollections.observableArrayList();
    private Receta seleccion;
    private HistoricoRecetas historico;

    @FXML
    private void initialize() {
        // Mapeo de columnas mínimo
        tcIdDespacho.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getId())));
        tcMedicamentoDespacho.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                (cd.getValue() == null || cd.getValue().getMedicamento() == null)
                        ? "" : String.valueOf(cd.getValue().getMedicamento().getNombre())));
        tcPacienteDespacho.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                (cd.getValue() == null || cd.getValue().getPaciente() == null)
                        ? "" : String.valueOf(cd.getValue().getPaciente().getNombre())));
        tcEstadoDespacho.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getEstado())));
        tcCantDespacho.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getCantidad())));
        // Doble click para seleccionar y cerrar
        tbvDespacho.setRowFactory(tv -> {
            TableRow<Receta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    seleccion = row.getItem();
                    cerrar();
                }
            });
            return row;
        });
        btnBuscarRecetasDespacho.setOnAction(e -> aplicarFiltro());
        btnSalirRecetasDespacho.setOnAction(e -> cerrar());
    }

    public void setHistorico(HistoricoRecetas historico) {
        this.historico = historico;
        respaldo.setAll(historico.getItems());         // respaldo para el filtro
        tbvDespacho.setItems(respaldo);
    }

    public Receta getSeleccion() {
        return seleccion;
    }

    private void aplicarFiltro() {
        String q = txtHistorialDespacho.getText();
        if (q == null || q.isBlank()) {
            tbvDespacho.setItems(FXCollections.observableArrayList(respaldo));
            return;
        }
        String query = q.trim().toLowerCase();
        ObservableList<Receta> filtradas = FXCollections.observableArrayList();
        for (Receta r : respaldo) {
            if (r == null) continue;
            String id = String.valueOf(r.getId());
            String med = (r.getMedicamento() == null) ? "" : String.valueOf(r.getMedicamento().getNombre());
            String pac = (r.getPaciente() == null) ? "" : String.valueOf(r.getPaciente().getNombre());
            String est = r.getEstado() == null ? "" : r.getEstado();
            if (id.toLowerCase().contains(query) || med.toLowerCase().contains(query) ||
                    pac.toLowerCase().contains(query) || est.toLowerCase().contains(query)) {
                filtradas.add(r);
            }
        }
        tbvDespacho.setItems(filtradas);
    }

    private void cerrar() {
        Stage stage = (Stage) btnSalirRecetasDespacho.getScene().getWindow();
        stage.close();
    }
}