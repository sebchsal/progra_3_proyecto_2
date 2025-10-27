package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Logica.MedicamentoLogica;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

public class AgregarMedicamentoController {
    @FXML private TextField txtCodigoAgregarMedicamento, txtNombreAgregarMedicamento, txtPresentacionAgregarMedicamento;
    @FXML private Button btnRegistrarMedicamento;

    private Medicamento medicamento;
    private boolean modoEdicion = false;

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

            guardarMedicamentoAsync(medicamento);
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

    private void guardarMedicamentoAsync(Medicamento m) {
        if (btnRegistrarMedicamento != null) btnRegistrarMedicamento.setDisable(true);

        Async.run(
                () -> {
                    try {
                        MedicamentoLogica logica = new MedicamentoLogica();
                        if (modoEdicion) {
                            return logica.update(m);
                        } else {
                            int nuevoId = logica.insert(m).getId();
                            m.setId(nuevoId);
                            return m;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                guardado -> {
                    if (btnRegistrarMedicamento != null) btnRegistrarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Medicamento actualizado (ID: " : "Medicamento guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();
                    ((Stage) txtNombreAgregarMedicamento.getScene().getWindow()).close();
                },
                ex -> {
                    if (btnRegistrarMedicamento != null) btnRegistrarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + ex.getMessage()).showAndWait();
                }
        );
    }
}
