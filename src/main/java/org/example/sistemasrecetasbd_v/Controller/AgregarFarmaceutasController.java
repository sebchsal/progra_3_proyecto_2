package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import org.example.sistemasrecetasbd_v.Logica.FarmaceutaLogica;
import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;

public class AgregarFarmaceutasController {
    @FXML private TextField txtIdentificacionAgregarFarmaceuta, txtNombreAgregarFarmaceuta;
    @FXML private Button btnRegistrarFarmaceuta, btnVolverFarmaceuta;
    @FXML private ProgressIndicator progFarmaceuta;

    private final FarmaceutaLogica logica = new FarmaceutaLogica();
    private Farmaceuta farmaceuta;
    private boolean modoEdicion = false;

    public void setFarmaceuta( Farmaceuta farmaceuta, boolean editar) {
        this.farmaceuta = farmaceuta;
        this.modoEdicion = editar;

        if (editar && farmaceuta != null) {
            txtIdentificacionAgregarFarmaceuta.setText(farmaceuta.getIdentificacion());
            txtNombreAgregarFarmaceuta.setText(farmaceuta.getNombre());
        }
    }

    @FXML private void registrarFarmaceuta() {
        try{
            String identificacion = txtIdentificacionAgregarFarmaceuta.getText().trim();
            String nombre = txtNombreAgregarFarmaceuta.getText().trim();
            if (identificacion.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor, complete todos los campos del formulario.");
                return;
            }

            if (!modoEdicion) {
                farmaceuta = new Farmaceuta(identificacion, nombre, identificacion);
            }else {
                farmaceuta.setIdentificacion(identificacion);
                farmaceuta.setNombre(nombre);
            }

           guardarFarmaceutaAsync(farmaceuta);
        }catch(Exception e){
            mostrarAlerta("Error al guardar los datos", e.getMessage());

        }
    }

    @FXML private void volverFarmaceuta() {
        try
        {
            Stage stage = (Stage) txtNombreAgregarFarmaceuta.getScene().getWindow();
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


    private void guardarFarmaceutaAsync(Farmaceuta f) {
        btnRegistrarFarmaceuta.setDisable(true);
        btnVolverFarmaceuta.setDisable(true);

        Async.run(
                () -> {
                    try {
                        if (modoEdicion) {
                            return logica.update(f);
                        } else {
                            int nuevoId = logica.insert(f).getId();
                            f.setId(nuevoId);
                            return f;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                guardado -> {
                    btnRegistrarFarmaceuta.setDisable(false);
                    btnVolverFarmaceuta.setDisable(false);
                    progFarmaceuta.setVisible(true);

                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Farmaceuta actualizado (ID: " : "Farmaceuta guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();
                    ((Stage) txtNombreAgregarFarmaceuta.getScene().getWindow()).close();
                },
                ex -> {
                    btnRegistrarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + ex.getMessage()).showAndWait();
                }
        );
    }
}
