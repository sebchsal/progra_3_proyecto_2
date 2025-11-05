package org.example.sistemasrecetasbd_v.Controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Data.GsonProvider;
import org.example.sistemasrecetasbd_v.Logica.RecetaLogica;
import org.example.sistemasrecetasbd_v.Model.Listas.HistoricoRecetas;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Servicios.UserSocketService;

import java.time.LocalDate;

public class DespachoController {
    @FXML private Button btnVolverReceta;

    @FXML private TextField txtIDReceta;
    @FXML private TextField txtPacienteReceta;
    @FXML private TextField txtIdentifcacionReceta;
    @FXML private TextField txtEstadoReceta;

    @FXML private DatePicker dtpFechaConfeccion;
    @FXML private DatePicker dtpFechaEntregaReceta;

    @FXML private TableView<Receta> tblReceta;
    @FXML private TableColumn<Receta, String> colIDReceta;
    @FXML private TableColumn<Receta, String> colMedicamentoReceta;
    @FXML private TableColumn<Receta, String> colCantidadReceta;
    @FXML private TableColumn<Receta, String> colDiasReceta;

    @FXML private TextArea txtAreaDetallesReceta;
    @FXML private ChoiceBox<String> cbxEstadoReceta;

    // Estado interno referencias compartidas
    private HistoricoRecetas historicoRecetas;
    private Receta seleccionada;
    private RecetaLogica recetaLogica;
    private final Gson gson = GsonProvider.get();

    // Carga de listas desde InicioController
    public void setListas(HistoricoRecetas historicoRecetas, RecetaLogica rl) {
        this.historicoRecetas = historicoRecetas;
        this.recetaLogica = rl;
    }

    @FXML private void initialize() {
        // Estados disponibles
        cbxEstadoReceta.setItems(FXCollections.observableArrayList("proceso", "lista", "entregada"));
        // Configurar columnas de la tabla de la receta seleccionada
        colIDReceta.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getId())));
        colMedicamentoReceta.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                (cd.getValue() == null || cd.getValue().getMedicamento() == null)
                        ? "" : String.valueOf(cd.getValue().getMedicamento().getNombre())));
        colCantidadReceta.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getCantidad())));
        colDiasReceta.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                cd.getValue() == null ? "" : String.valueOf(cd.getValue().getCantidad())));
    }

    @FXML private void abrirHistorialParaElegir() {
        // 1) Verificar que existan recetas creadas
        if (historicoRecetas == null || historicoRecetas.getItems() == null || historicoRecetas.getItems().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No hay recetas registradas en el historial.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/HistorialRecetas.fxml"));
            Parent root = loader.load();
            HistorialRecetasController ctrl = loader.getController();
            ctrl.setHistorico(historicoRecetas);

            Stage stage = new Stage();
            stage.setTitle("Historial de Recetas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Receta seleccion = ctrl.getSeleccion();
            if (seleccion != null) {
                this.seleccionada = seleccion;
                mostrarReceta(seleccion);
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al mostrar receta.");
            ex.printStackTrace();
        }
    }

    @FXML private void mostrarReceta(Receta r) {
        if (r == null) return;
        txtIDReceta.setText(String.valueOf(r.getId()));
        if (r.getPaciente() != null) {
            txtPacienteReceta.setText(String.valueOf(r.getPaciente().getNombre()));
            txtIdentifcacionReceta.setText(String.valueOf(r.getPaciente().getIdentificacion()));
        } else {
            txtPacienteReceta.setText("");
            txtIdentifcacionReceta.setText("");
        }
        txtEstadoReceta.setText(String.valueOf(r.getEstado() == null ? "" : r.getEstado()));

        if (r.getFechaConfeccion() != null) {
            dtpFechaConfeccion.setValue(r.getFechaConfeccion());
        } else {
            dtpFechaConfeccion.setValue(null);
        }
        if (r.getFechaEntrega() != null) {
            dtpFechaEntregaReceta.setValue(r.getFechaEntrega());
        } else {
            dtpFechaEntregaReceta.setValue(null);
        }
        // Detalles
        txtAreaDetallesReceta.setText(r.getDetalle());
        // Mostrar en la tabla solo la receta seleccionada (vista compacta)
        ObservableList<Receta> unica = FXCollections.observableArrayList();
        unica.add(r);
        tblReceta.setItems(unica);
        // Estado preseleccionado en el choicebox si coincide
        if (r.getEstado() != null && cbxEstadoReceta.getItems().contains(r.getEstado())) {
            cbxEstadoReceta.getSelectionModel().select(r.getEstado());
        }
        cbxEstadoReceta.getSelectionModel().clearSelection();
    }

    @FXML private void guardarCambios() {
        if (seleccionada == null) {
            mostrarAlerta("Error","Seleccione una receta.");
            return;
        }

        String nuevoEstado = cbxEstadoReceta.getSelectionModel().getSelectedItem();
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            mostrarAlerta("Error","Seleccione un estado para la receta.");
            return;
        }

        try {
            // Actualizar objeto en memoria
            seleccionada.setEstado(nuevoEstado);
            LocalDate confe = dtpFechaConfeccion.getValue();
            LocalDate entr = dtpFechaEntregaReceta.getValue();
            if (confe != null) seleccionada.setFechaConfeccion(confe);
            if (entr != null)   seleccionada.setFechaEntrega(entr);
            String det = txtAreaDetallesReceta.getText();
            if (det != null) seleccionada.setDetalle(det.trim());
            // Persistir/actualizar en HistoricoRecetas
            actualizarEnHistoricoSinDuplicar(historicoRecetas, seleccionada);

            Async.run(() -> {
                try {
                    UserSocketService servicio = new UserSocketService();
                    String json = """
                    {
                         "tipo": "receta",
                         "op": "update",
                         "data": %s
                    }
                    \s""".formatted(gson.toJson(seleccionada));

                    String respuesta = servicio.enviar(json);
                    return gson.fromJson(respuesta, Receta.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, actualizada -> {
                if (actualizada != null) {
                    txtEstadoReceta.setText(actualizada.getEstado());
                    tblReceta.refresh();
                    volverAInicio();
                    mostrarAlerta("Éxito", "Receta actualizada correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar la receta en el servidor.");
                }
            }, ex -> {
                mostrarAlerta("Error al guardar los cambios", ex.getMessage());
            });

        } catch (Exception ex) {
            mostrarAlerta("No se pudo guardar: ", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarEnHistoricoSinDuplicar(HistoricoRecetas historico, Receta r) {
        if (historico == null || historico.getItems() == null || r == null) return;
        var lista = historico.getItems();
        for (int i = 0; i < lista.size(); i++) {
            Receta cur = lista.get(i);
            // Misma referencia o mismo id => reemplazar posición y salir
            if (cur == r || (cur != null && java.util.Objects.equals(cur.getId(), r.getId()))) {
                // Si es la misma referencia, set(i, r) no duplica
                lista.set(i, r);
                return;
            }
        }
    }

    @FXML private void volverAInicio() {
        Stage stage = (Stage) btnVolverReceta.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

