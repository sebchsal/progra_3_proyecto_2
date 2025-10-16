package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;

public class AgregarFarmaceutasController {
    @FXML private TextField txtIdentificacionAgregarFarmaceuta, txtNombreAgregarFarmaceuta;
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

            Stage stage = (Stage) txtNombreAgregarFarmaceuta.getScene().getWindow();
            stage.setUserData(farmaceuta);
            stage.close();
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
}
