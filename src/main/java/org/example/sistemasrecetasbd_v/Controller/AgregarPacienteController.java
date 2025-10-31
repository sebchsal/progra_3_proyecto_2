package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Logica.PacienteLogica;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;

import java.time.LocalDate;

public class AgregarPacienteController {
    @FXML private TextField txtIdentificacionAgregarPaciente,
            txtNombreAgregarPaciente, txtTelefonoAgregarPaciente;
    @FXML private DatePicker dtpFechaAgregarPaciente;
    @FXML private Button btnRegistrarPaciente, btnVolverPaciente;
    @FXML private ProgressIndicator progPaciente;

    private Paciente paciente;
    private boolean modoEdicion = false;

    private TableView<Paciente> tablaDestino;
    public void setTablaDestino(TableView<Paciente> t) {
        this.tablaDestino = t;
    }

    // Recibe la lista y los pacientes
    public void setPaciente(Paciente paciente, boolean editar) {
        this.paciente = paciente;
        this.modoEdicion = editar;

        if (editar && paciente != null) {
            txtIdentificacionAgregarPaciente.setText(paciente.getIdentificacion());
            txtNombreAgregarPaciente.setText(paciente.getNombre());
            dtpFechaAgregarPaciente.setValue(paciente.getFechaNacimiento());
            txtTelefonoAgregarPaciente.setText(paciente.getTelefono());
            btnRegistrarPaciente.setText("Guardar");
        } else {
            btnRegistrarPaciente.setText("Registrar");
        }
    }

    // registra los pacientes en la lista o tambien los pacientes modificados
    @FXML
    private void registrarPaciente() {
        try {
            String identificacion = txtIdentificacionAgregarPaciente.getText().trim();
            String nombre = txtNombreAgregarPaciente.getText().trim();
            LocalDate fechaNac = dtpFechaAgregarPaciente.getValue();
            String telefono = txtTelefonoAgregarPaciente.getText().trim();

            if (identificacion.isEmpty() || nombre.isEmpty() || fechaNac == null || telefono.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor complete todos los campos.");
                return;
            }

            if (!modoEdicion) {
                // NO agregar a la lista aquí, solo crear el objeto y devolverlo
                paciente = new Paciente(identificacion, nombre, fechaNac, telefono);
            } else {
                paciente.setIdentificacion(identificacion);
                paciente.setNombre(nombre);
                paciente.setFechaNacimiento(fechaNac);
                paciente.setTelefono(telefono);
            }
            guardarPacienteAsync(paciente);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    // vuelve a la pantalla del inicio o solo cierra la ventana del formulario
    @FXML private void volver() {
        Stage stage = (Stage) txtNombreAgregarPaciente.getScene().getWindow();
        stage.setUserData(null);
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void guardarPacienteAsync(Paciente p) {
        btnRegistrarPaciente.setDisable(true);
        btnVolverPaciente.setDisable(true);

        Async.run(
                () -> {
                    try {
                        PacienteLogica logica = new PacienteLogica();
                        if (modoEdicion) {
                            return logica.update(p);
                        } else {
                            int nuevoId = logica.insert(p).getId();
                            p.setId(nuevoId);
                            return p;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                guardado -> {
                    btnRegistrarPaciente.setDisable(false);
                    btnVolverPaciente.setDisable(false);
                    progPaciente.setVisible(true);
                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Paciente actualizado (ID: " : "Paciente guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();
                    ((Stage) txtNombreAgregarPaciente.getScene().getWindow()).close();
                },
                ex -> {
                    btnRegistrarPaciente.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + ex.getMessage()).showAndWait();
                }
        );
    }
}
