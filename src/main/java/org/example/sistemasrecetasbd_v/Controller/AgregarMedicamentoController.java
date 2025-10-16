package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

public class AgregarMedicamentoController {
    @FXML private TextField txtCodigoAgregarMedicamento, txtNombreAgregarMedicamento, txtPresentacionAgregarMedicamento;

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

            Stage stage = (Stage) txtNombreAgregarMedicamento.getScene().getWindow();
            stage.setUserData(medicamento);
            stage.close();
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
}
