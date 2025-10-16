package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;

public class DetalleController {
    @FXML private TextField txtCodigoDetalle;
    @FXML private TextField txtEstadoDetalle;
    @FXML private TextField txtCantidadDetalle;
    @FXML private TextField txtMedicamentoDetalle;
    @FXML private TextField txtPacienteDetalle;
    @FXML private DatePicker dtpFechaCDetalle;
    @FXML private DatePicker dtpFechaEDetalle;
    @FXML private TextArea txtaDetalle;
    @FXML private Button btnVolverDetalle;

    public void setReceta(Receta receta) {
        // Llenar los campos con los datos de la receta
        txtCodigoDetalle.setText(String.valueOf(receta.getId()));
        txtEstadoDetalle.setText(receta.getEstado());
        txtCantidadDetalle.setText(String.valueOf(receta.getCantidad()));
        txtMedicamentoDetalle.setText(receta.getMedicamento().getNombre());
        txtPacienteDetalle.setText(receta.getPaciente().getNombre());
        if (receta.getFechaConfeccion() != null) {
            dtpFechaCDetalle.setValue(receta.getFechaConfeccion());
        }
        if (receta.getFechaEntrega() != null) {
            dtpFechaEDetalle.setValue(receta.getFechaEntrega());
        }
        txtaDetalle.setText(receta.getDetalle());
    }

    @FXML private void initialize() {
        // DatePickers no editables
        dtpFechaCDetalle.setEditable(false);
        dtpFechaEDetalle.setEditable(false);
    }

    @FXML private void volverAInicio() {
        Stage stage = (Stage) btnVolverDetalle.getScene().getWindow();
        stage.close();
    }
}