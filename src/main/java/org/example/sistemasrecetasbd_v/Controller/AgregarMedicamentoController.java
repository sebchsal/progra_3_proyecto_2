package org.example.sistemasrecetasbd_v.Controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Data.GsonProvider;
import org.example.sistemasrecetasbd_v.Servicios.UserSocketService;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

public class AgregarMedicamentoController {
    @FXML private TextField txtCodigoAgregarMedicamento, txtNombreAgregarMedicamento, txtPresentacionAgregarMedicamento;
    @FXML private Button btnRegistrarMedicamento, btnVolverMedicamento;
    @FXML private ProgressIndicator progMedicamento;

    private final Gson gson = GsonProvider.get();
    private Medicamento medicamento;
    private boolean modoEdicion = false;

    private TableView<Medicamento> tablaDestino;
    public void setTablaDestino(TableView<Medicamento> t) {
        this.tablaDestino = t;
    }

    public void setMedicamento(Medicamento medicamento, boolean editar) {
        this.medicamento = medicamento;
        this.modoEdicion = editar;

        if (editar && medicamento != null) {
            txtCodigoAgregarMedicamento.setText(medicamento.getCodigo());
            txtNombreAgregarMedicamento.setText(medicamento.getNombre());
            txtPresentacionAgregarMedicamento.setText(medicamento.getTipoPresentacion());
        }
    }

    @FXML private void registrarMedicamento() {
        try {
            String codigo = txtCodigoAgregarMedicamento.getText().trim();
            String nombre = txtNombreAgregarMedicamento.getText().trim();
            String presentacion = txtPresentacionAgregarMedicamento.getText().trim();

            if (nombre.isEmpty() || presentacion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor complete todos los campos.");
                return;
            }
            if (!modoEdicion) {
                medicamento = new Medicamento(codigo, nombre, presentacion);
            } else {
                medicamento.setCodigo(codigo);
                medicamento.setNombre(nombre);
                medicamento.setTipoPresentacion(presentacion);
            }

            guardarMedicamentoAsync(medicamento, tablaDestino);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML private void volverMedicamento() {
        try
        {
            Stage stage = (Stage) txtNombreAgregarMedicamento.getScene().getWindow();
            stage.setUserData(null);
            stage.close();
        }
        catch (Exception error) {
            mostrarAlerta("Error al guardar los datos", error.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void guardarMedicamentoAsync(Medicamento m, TableView<Medicamento> tablaMedicamento) {
        btnRegistrarMedicamento.setDisable(true);
        btnVolverMedicamento.setDisable(true);
        progMedicamento.setVisible(true);

        Async.run(
                () -> {
                    try {
                        UserSocketService servicio = new UserSocketService();
                        String json = """
                    {
                      "tipo": "medicamento",
                      "op": "%s",
                      "data": %s
                    }
                    """.formatted(modoEdicion ? "update" : "create", gson.toJson(m));

                        String respuesta = servicio.enviar(json);
                        Medicamento guardado = gson.fromJson(respuesta, Medicamento.class);

                        if (!modoEdicion && guardado != null)
                            m.setId(guardado.getId());

                        return m;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                guardado -> {
                    progMedicamento.setVisible(false);
                    btnRegistrarMedicamento.setDisable(false);
                    btnVolverMedicamento.setDisable(false);

                    if (tablaMedicamento != null) {
                        if (modoEdicion) tablaMedicamento.refresh();
                        else tablaMedicamento.getItems().add(guardado);
                    }

                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Medicamento actualizado (ID: " : "Medicamento guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();

                    ((Stage) txtNombreAgregarMedicamento.getScene().getWindow()).close();
                },
                ex -> {
                    progMedicamento.setVisible(false);
                    btnRegistrarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar el medicamento: " + ex.getMessage()).showAndWait();
                }
        );
    }

}
