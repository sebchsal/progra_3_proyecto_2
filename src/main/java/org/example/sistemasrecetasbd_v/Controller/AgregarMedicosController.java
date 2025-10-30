package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import org.example.sistemasrecetasbd_v.Logica.MedicoLogica;
import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

public class AgregarMedicosController {
    @FXML private TextField txtIdentificacionAgregarMedico,
            txtNombreAgregarMedico, txtEspecialidadAgregarMedico;
    @FXML private Button btnRegistrarMedico, btnVolverMedico;
    @FXML private ProgressIndicator progMedico;

    private final MedicoLogica logica = new MedicoLogica();
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
            guardarMedicoAsync(medico);

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

    private void guardarMedicoAsync(Medico m) {
        btnRegistrarMedico.setDisable(true);
        btnVolverMedico.setDisable(true);
        Async.run(
                () -> {
                    try {
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
                    progMedico.setVisible(false);
                    btnRegistrarMedico.setDisable(false);
                    btnVolverMedico.setDisable(true);

                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Médico actualizado (ID: " : "Médico guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();

                    ((Stage) txtNombreAgregarMedico.getScene().getWindow()).close();
                },
                ex -> {
                    btnRegistrarMedico.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + ex.getMessage()).showAndWait();
                }
        );
    }
}
