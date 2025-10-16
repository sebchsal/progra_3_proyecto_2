package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

public class AgregarMedicosController {
    @FXML private TextField txtIdentificacionAgregarMedico,
            txtNombreAgregarMedico, txtEspecialidadAgregarMedico;
    @FXML private Button btnRegistrarMedico;

    private Medico medico;
    private boolean modoEdicion = false;

    // Recibe la lista y el medico o tambien los medicos modificados
    public void setMedico(Medico medico, boolean editar) {
        this.medico = medico;
        this.modoEdicion = editar;

        if (editar && medico != null) {
            txtIdentificacionAgregarMedico.setText(medico.getIdentificacion());
            txtNombreAgregarMedico.setText(medico.getNombre());
            txtEspecialidadAgregarMedico.setText(medico.getEspecialidad());
            btnRegistrarMedico.setText("Guardar");
        } else {
            btnRegistrarMedico.setText("Registrar");
        }
    }


    // registra el medico
    @FXML private void registrarMedico() {
        try {
            String identificacion = txtIdentificacionAgregarMedico.getText().trim();
            String nombre = txtNombreAgregarMedico.getText().trim();
            String especialidad = txtEspecialidadAgregarMedico.getText().trim();

            if (identificacion.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor complete todos los campos.");
                return;
            }

            if (!modoEdicion) {
                medico = new Medico(identificacion, nombre, identificacion, especialidad);
            } else {
                medico.setIdentificacion(identificacion);
                medico.setNombre(nombre);
                medico.setEspecialidad(especialidad);
                medico.setClave(identificacion);
            }
            Stage stage = (Stage) txtNombreAgregarMedico.getScene().getWindow();
            stage.setUserData(medico);
            stage.close();
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    // vuelve a la pantalla del inicio o solo cierra la ventana
    @FXML private void volverMedico() {
        try
        {
            Stage stage = (Stage) txtNombreAgregarMedico.getScene().getWindow();
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
